package com.oceanview.service;

import com.oceanview.model.GuestReservation;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

//Concrete Observer for email notification delivery.

public class EmailNotificationObserver implements IReservationObserver {

    
    private static final Logger EMAIL_LOGGER =
            Logger.getLogger(EmailNotificationObserver.class.getName());

   
    private static final String SMTP_HOST = "smtp.gmail.com";

   
    private static final String SMTP_PORT = "587";

    
    private static final String SMTP_USERNAME = "landorelieflk@gmail.com";

    
    private static final String SMTP_PASSWORD = "qgio kmkf cuft qyvp";

    
    private static final String RESORT_NAME = "Ocean View Resort";

   
    private static final String RESORT_LOCATION = "Galle, Sri Lanka";

    @Override
    public void onReservationCreated(GuestReservation reservation) {

        String guestEmail = reservation.getGuestEmail();

        // Check if guest provided an email address
        if (guestEmail == null || guestEmail.trim().isEmpty()) {
            EMAIL_LOGGER.info("No email address provided for reservation: "
                    + reservation.getReservationNumber()
                    + " - skipping email notification");
            return;
        }

        String subject = RESORT_NAME
                + " - Reservation Confirmation "
                + reservation.getReservationNumber();

        String emailBody = buildConfirmationEmailBody(reservation);

        // Send or log the email
        sendEmailMessage(guestEmail, subject, emailBody);
    }

 
    @Override
    public void onReservationStatusChanged(GuestReservation reservation,
                                           String newStatus) {

        String guestEmail = reservation.getGuestEmail();

        if (guestEmail == null || guestEmail.trim().isEmpty()) {
            EMAIL_LOGGER.info("No email for status notification: "
                    + reservation.getReservationNumber());
            return;
        }

        String subject = RESORT_NAME
                + " - Reservation " + newStatus + " "
                + reservation.getReservationNumber();

        String emailBody = buildStatusUpdateEmailBody(
                reservation, newStatus);

        sendEmailMessage(guestEmail, subject, emailBody);
    }

   
    @Override
    public String getObserverName() {
        return "EmailNotificationObserver";
    }

 
    private String buildConfirmationEmailBody(
            GuestReservation reservation) {

        StringBuilder emailContent = new StringBuilder();

        emailContent.append("<html><body style='font-family: Poppins, Arial, sans-serif;'>");
        emailContent.append("<div style='max-width: 600px; margin: 0 auto; ");
        emailContent.append("border: 1px solid #ddd; border-radius: 10px;'>");

        // Header
        emailContent.append("<div style='background: linear-gradient(135deg, #6c3483, #8e44ad); ");
        emailContent.append("color: white; padding: 20px; text-align: center; ");
        emailContent.append("border-radius: 10px 10px 0 0;'>");
        emailContent.append("<h1 style='margin: 0;'>").append(RESORT_NAME).append("</h1>");
        emailContent.append("<p style='margin: 5px 0 0 0;'>").append(RESORT_LOCATION).append("</p>");
        emailContent.append("</div>");

        // Body content
        emailContent.append("<div style='padding: 25px;'>");
        emailContent.append("<h2 style='color: #6c3483;'>Reservation Confirmed!</h2>");
        emailContent.append("<p>Dear <strong>").append(reservation.getGuestName());
        emailContent.append("</strong>,</p>");
        emailContent.append("<p>Your reservation has been confirmed. ");
        emailContent.append("Here are your booking details:</p>");

        // Details table
        emailContent.append("<table style='width: 100%; border-collapse: collapse; margin: 15px 0;'>");
        emailContent.append("<tr><td style='padding: 8px; border-bottom: 1px solid #eee; font-weight: bold;'>");
        emailContent.append("Reservation Number</td><td style='padding: 8px; border-bottom: 1px solid #eee;'>");
        emailContent.append(reservation.getReservationNumber()).append("</td></tr>");
        emailContent.append("<tr><td style='padding: 8px; border-bottom: 1px solid #eee; font-weight: bold;'>");
        emailContent.append("Room Type</td><td style='padding: 8px; border-bottom: 1px solid #eee;'>");
        emailContent.append(reservation.getRoomType()).append("</td></tr>");
        emailContent.append("<tr><td style='padding: 8px; border-bottom: 1px solid #eee; font-weight: bold;'>");
        emailContent.append("Check-In Date</td><td style='padding: 8px; border-bottom: 1px solid #eee;'>");
        emailContent.append(reservation.getCheckInDate()).append("</td></tr>");
        emailContent.append("<tr><td style='padding: 8px; border-bottom: 1px solid #eee; font-weight: bold;'>");
        emailContent.append("Check-Out Date</td><td style='padding: 8px; border-bottom: 1px solid #eee;'>");
        emailContent.append(reservation.getCheckOutDate()).append("</td></tr>");
        emailContent.append("</table>");

        emailContent.append("<p>We look forward to welcoming you!</p>");
        emailContent.append("<p>Warm regards,<br/><strong>");
        emailContent.append(RESORT_NAME).append(" Team</strong></p>");
        emailContent.append("</div>");

        // Footer
        emailContent.append("<div style='background: #f4f6f9; padding: 15px; ");
        emailContent.append("text-align: center; font-size: 12px; color: #666; ");
        emailContent.append("border-radius: 0 0 10px 10px;'>");
        emailContent.append("<p>&copy; 2026 ").append(RESORT_NAME);
        emailContent.append(" | ").append(RESORT_LOCATION).append("</p>");
        emailContent.append("</div>");

        emailContent.append("</div></body></html>");

        return emailContent.toString();
    }

    // Builds HTML email body for status update notification.
     
    private String buildStatusUpdateEmailBody(
            GuestReservation reservation, String newStatus) {

        StringBuilder emailContent = new StringBuilder();

        emailContent.append("<html><body style='font-family: Poppins, Arial, sans-serif;'>");
        emailContent.append("<div style='max-width: 600px; margin: 0 auto; ");
        emailContent.append("border: 1px solid #ddd; border-radius: 10px;'>");

        // Header
        emailContent.append("<div style='background: linear-gradient(135deg, #6c3483, #8e44ad); ");
        emailContent.append("color: white; padding: 20px; text-align: center; ");
        emailContent.append("border-radius: 10px 10px 0 0;'>");
        emailContent.append("<h1 style='margin: 0;'>").append(RESORT_NAME).append("</h1>");
        emailContent.append("</div>");

        // Body content
        emailContent.append("<div style='padding: 25px;'>");
        emailContent.append("<h2 style='color: #6c3483;'>Reservation Update</h2>");
        emailContent.append("<p>Dear <strong>").append(reservation.getGuestName());
        emailContent.append("</strong>,</p>");
        emailContent.append("<p>Your reservation <strong>");
        emailContent.append(reservation.getReservationNumber());
        emailContent.append("</strong> status has been updated to: ");
        emailContent.append("<strong style='color: #8e44ad;'>").append(newStatus);
        emailContent.append("</strong></p>");

        if ("Cancelled".equals(newStatus)
                && reservation.getCancelReason() != null) {
            emailContent.append("<p>Reason: ")
                    .append(reservation.getCancelReason()).append("</p>");
        }

        emailContent.append("<p>Warm regards,<br/><strong>");
        emailContent.append(RESORT_NAME).append(" Team</strong></p>");
        emailContent.append("</div>");

        // Footer
        emailContent.append("<div style='background: #f4f6f9; padding: 15px; ");
        emailContent.append("text-align: center; font-size: 12px; color: #666; ");
        emailContent.append("border-radius: 0 0 10px 10px;'>");
        emailContent.append("<p>&copy; 2026 ").append(RESORT_NAME).append("</p>");
        emailContent.append("</div>");

        emailContent.append("</div></body></html>");

        return emailContent.toString();
    }

    //Sends an email message via SMTP or logs it in demo mode.
    
    private void sendEmailMessage(String recipientEmail,
                                  String subject, String htmlBody) {

        // Check if SMTP is configured for real email delivery
        if (SMTP_USERNAME.isEmpty() || SMTP_PASSWORD.isEmpty()) {
            // Demo mode: log the email content instead of sending
            EMAIL_LOGGER.info(
                    "=== EMAIL NOTIFICATION (Demo Mode) ===\n"
                            + "To: " + recipientEmail + "\n"
                            + "Subject: " + subject + "\n"
                            + "Status: Logged (SMTP not configured)\n"
                            + "======================================");
            return;
        }

        try {
            // Configure SMTP properties for Gmail
            Properties mailProperties = new Properties();
            mailProperties.put("mail.smtp.auth", "true");
            mailProperties.put("mail.smtp.starttls.enable", "true");
            mailProperties.put("mail.smtp.host", SMTP_HOST);
            mailProperties.put("mail.smtp.port", SMTP_PORT);
            mailProperties.put("mail.smtp.ssl.trust", SMTP_HOST);

            // Create authenticated mail session
            Session mailSession = Session.getInstance(mailProperties,
                    new Authenticator() {
                        @Override
                        protected PasswordAuthentication
                        getPasswordAuthentication() {
                            return new PasswordAuthentication(
                                    SMTP_USERNAME, SMTP_PASSWORD);
                        }
                    });

            // Build the email message
            Message emailMessage = new MimeMessage(mailSession);
            emailMessage.setFrom(
                    new InternetAddress(SMTP_USERNAME, RESORT_NAME));
            emailMessage.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(recipientEmail));
            emailMessage.setSubject(subject);
            emailMessage.setContent(htmlBody, "text/html; charset=UTF-8");

            // Send the email
            Transport.send(emailMessage);

            EMAIL_LOGGER.info("Email sent successfully to: "
                    + recipientEmail);

        } catch (Exception mailException) {
            // Email failure should not crash the main operation
            EMAIL_LOGGER.log(Level.WARNING,
                    "Failed to send email to: " + recipientEmail,
                    mailException);
        }
    }
}