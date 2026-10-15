package Servicio;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.Multipart;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Properties;

/**
 * Envío de correo por HTTPS (API de Brevo) con respaldo SMTP de Gmail.
 *
 * Railway bloquea el tráfico SMTP saliente salvo en el plan Pro, así que el
 * transporte principal usa la API v3 de Brevo por HTTPS (puerto 443, que
 * Railway sí permite). Si no hay API key, se intenta Gmail SMTP como respaldo
 * (útil en entornos locales).
 *
 * Configuración vía variables de entorno (nunca hardcodeada):
 *   - EMAIL_API_KEY         : API key v3 de Brevo (transporte principal)
 *   - EMAIL_SENDER          : dirección verificada en Brevo (remitente); si no
 *                             está, se usa SMTP_USER
 *   - SMTP_USER             : dirección Gmail que envía (respaldo SMTP / remitente)
 *   - SMTP_APP_PASSWORD     : contraseña de aplicación de Gmail (respaldo SMTP)
 *
 * Si no hay nada configurado la devolución explica la configuración necesaria
 * (fallo seguro).
 */
public class CorreoServicio {

    private static final String BREVO_URL = "https://api.brevo.com/v3/smtp/email";
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final int SMTP_PORT = 587;

    /** Devuelve true si hay algún medio de envío configurado. */
    public static boolean estaConfigurado() {
        return getVar("EMAIL_API_KEY") != null
                || (getVar("SMTP_USER") != null && getVar("SMTP_APP_PASSWORD") != null);
    }

    /**
     * Envía un correo en HTML.
     *
     * @return null si se envió bien, o un mensaje de error si falló.
     */
    public String enviar(String destinatario, String asunto, String cuerpoHtml) {
        String apiKey = getVar("EMAIL_API_KEY");
        if (apiKey != null) {
            String remitente = getVar("EMAIL_SENDER") != null ? getVar("EMAIL_SENDER") : getVar("SMTP_USER");
            if (remitente == null) {
                return "Falta el remitente (EMAIL_SENDER o SMTP_USER).";
            }
            return enviarPorBrevo(apiKey, remitente, destinatario, asunto, cuerpoHtml);
        }

        String usuario = getVar("SMTP_USER");
        String pass = getVar("SMTP_APP_PASSWORD");
        if (usuario != null && pass != null) {
            return enviarPorSmtp(usuario, pass, destinatario, asunto, cuerpoHtml);
        }

        return "No hay servicio de correo configurado (EMAIL_API_KEY o SMTP_USER+SMTP_APP_PASSWORD).";
    }

    private String enviarPorBrevo(String apiKey, String remitente, String destinatario, String asunto, String cuerpoHtml) {
        String json = "{"
                + "\"sender\":{\"name\":" + escapeJson("ClassControl") + ",\"email\":" + escapeJson(remitente) + "},"
                + "\"to\":[{\"email\":" + escapeJson(destinatario) + ",\"name\":" + escapeJson("Usuario") + "}],"
                + "\"subject\":" + escapeJson(asunto) + ","
                + "\"htmlContent\":" + escapeJson(cuerpoHtml)
                + "}";

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BREVO_URL))
                    .timeout(Duration.ofSeconds(20))
                    .header("accept", "application/json")
                    .header("api-key", apiKey)
                    .header("content-type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                    .build();

            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(15))
                    .build();

            HttpResponse<String> respuesta = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (respuesta.statusCode() >= 200 && respuesta.statusCode() < 300) {
                System.out.println("CorreoServicio - enviado vía Brevo a " + destinatario + " (HTTP " + respuesta.statusCode() + ")");
                return null;
            }

            String detalle = respuesta.body() != null ? respuesta.body() : "";
            System.out.println("CorreoServicio - Brevo respondió HTTP " + respuesta.statusCode() + ": "
                    + detalle.substring(0, Math.min(300, detalle.length())));
            return "El proveedor de correo rechazó el envío (HTTP " + respuesta.statusCode() + "). Revisa EMAIL_API_KEY y el remitente verificado.";
        } catch (Exception e) {
            System.out.println("CorreoServicio - error al enviar por Brevo: " + e.getMessage());
            e.printStackTrace();
            return "No se pudo enviar el correo: " + e.getMessage();
        }
    }

    private String enviarPorSmtp(String usuario, String pass, String destinatario, String asunto, String cuerpoHtml) {
        Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", String.valueOf(SMTP_PORT));
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.connectiontimeout", "15000");
        props.put("mail.smtp.timeout", "20000");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(usuario, pass);
            }
        });

        try {
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(usuario, "ClassControl"));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            msg.setSubject(asunto, "UTF-8");

            MimeBodyPart contenido = new MimeBodyPart();
            contenido.setContent(cuerpoHtml, "text/html; charset=UTF-8");

            Multipart multipart = new MimeMultipart("alternative");
            multipart.addBodyPart(contenido);

            msg.setContent(multipart);
            Transport.send(msg);
            return null;
        } catch (Exception e) {
            System.out.println("CorreoServicio - error al enviar por SMTP: " + e.getMessage());
            e.printStackTrace();
            return "No se pudo enviar el correo: " + e.getMessage();
        }
    }

    private static String escapeJson(String texto) {
        if (texto == null) {
            return "\"\"";
        }
        StringBuilder sb = new StringBuilder();
        sb.append('"');
        for (int i = 0; i < texto.length(); i++) {
            char c = texto.charAt(i);
            switch (c) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        sb.append('"');
        return sb.toString();
    }

    private static String getVar(String nombre) {
        String valor = System.getenv(nombre);
        if (valor == null || valor.trim().isEmpty()) {
            return null;
        }
        return valor.trim();
    }
}