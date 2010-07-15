package edu.ucla.loni.ccb.itools.servlet;

import javax.mail.*;
import javax.mail.internet.*;


import java.util.*;

/**
  * A simple email sender class.
  */
public class SimpleSender
{

  /**
    * MAIN method to send a message given on the command line.
    */
  public static void main(String args[])
  {
    try
    {
      String smtpServer=args[0];
      String to=args[1];
      String from=args[2];
      String subject=args[3];
      String body=args[4];

      send(smtpServer, to, from, subject, body);
    }
    catch (Exception ex)
    {
      System.out.println("Usage: java SimpleSender"
       +" smtpServer toAddress fromAddress subjectText bodyText");
    }

    System.exit(0);
  }

  /**
    * "send" method to send the message.
    */
  public static void send(String smtpServer, String to, String from
   , String subject, String body)
  {
    try
    {
      Properties props = System.getProperties();

      // -- Attaching to default Session, or we could start a new one --
      // -- Could use Session.getTransport() and Transport.connect()
      // , but assume we're using SMTP --

      props.put("mail.smtp.host", smtpServer);
      Session session = Session.getDefaultInstance(props, null);

      // -- Create a new message --
      Message msg = new MimeMessage(session);

      // -- Set the FROM and TO fields --
      msg.setFrom(new InternetAddress(from));
      msg.setRecipients(Message.RecipientType.TO,
        InternetAddress.parse(to, false));

      // -- We could include CC recipients too --
      // if (cc != null)
      // msg.setRecipients(Message.RecipientType.CC
      // ,InternetAddress.parse(cc, false));

      // -- Set the subject and body text --
      msg.setSubject(subject);
      msg.setText(body);

      // -- Set some other header information --
      //msg.setHeader("X-Mailer", "LOTONtechEmail");
      msg.setSentDate(new Date());

      // -- Send the message --
      Transport.send(msg);

      System.out.println("Message sent OK.");
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }
}
