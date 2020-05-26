<?php
require_once 'include/SQLWrapper.php';
require_once 'include/mail_config.php';
require_once 'lib/PHPMailerAutoload.php';
$db = new SQLWrapper();
if (isset($_POST['email']) && isset($_POST['password'])) {
    if ($db->getUser($_POST['email'], $_POST['password'])) {
        $db->updateUser($_POST['email'], $_POST['newEmail'], $_POST['name'], $_POST['goal']);
        if ($_POST['email'] != $_POST['newEmail'])
        {
            $mail = new PHPMailer();
            $mail->IsSMTP();
            $mail->SMTPAuth = TRUE;
            $mail->SMTPSecure = "tls";
            $mail->SMTPDebug = 2;
            $mail->Port     = port;
            $mail->Username = mailUser;
            $mail->Password = mailPassword;
            $mail->Host     = mailHost;
            $mail->Mailer   = mailer;
            $mail->SetFrom(senderEmail, senderName);
            $mail->AddAddress($_POST['email']);
            $mail->AddAddress($_POST['newEmail']);
            $mail->Subject = "LiftLogger Email Update";
            $mail->Body =  "Your account ". $_POST['newEmail'] . " has been updated to " . $_POST['newEmail'];
            $mail->IsHTML(false);
            $mail->send();
        }
        echo "updated";
    } else {
        echo "Error";
    }

}