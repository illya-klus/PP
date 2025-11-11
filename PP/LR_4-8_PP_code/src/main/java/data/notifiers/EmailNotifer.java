package data.notifiers;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;
public class EmailNotifer {
    private static final String FROM = "illyaklus2@gmail.com";
    private static final String TO = "illia.klus.oi.2024@lpnu.ua";
    private static final String PASSWORD = "tyhi vyod zuux rylz";

    public static void sendCriticalError(String subject, String message, Exception exception) {
        new Thread(() -> {
            try {
                Properties props = new Properties();
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true"); // 🔑 STARTTLS
                props.put("mail.smtp.host", "smtp.gmail.com");
                props.put("mail.smtp.port", "587");
                props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

                Session session = Session.getInstance(props,
                        new Authenticator() {
                            protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication(FROM, PASSWORD);
                            }
                        });

                Message email = new MimeMessage(session);
                email.setFrom(new InternetAddress(FROM));
                email.setRecipients(Message.RecipientType.TO, InternetAddress.parse(TO));
                email.setSubject("🚨 CRITICAL: " + subject);

                String body = "CRITICAL ERROR REPORT\n\n" +
                        "Application: Banking App\n" +
                        "Time: " + java.time.LocalDateTime.now() + "\n" +
                        "Error: " + message + "\n\n";

                if (exception != null) {
                    body += "Exception: " + exception.getMessage() + "\n\n" +
                            "Stack Trace:\n" + getStackTrace(exception);
                }

                email.setText(body);
                Transport.send(email);
                System.out.println("✅ Critical error email sent successfully!");

            } catch (Exception e) {
                System.err.println("❌ Failed to send email: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    private static String getStackTrace(Exception e) {
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.PrintWriter pw = new java.io.PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
}
