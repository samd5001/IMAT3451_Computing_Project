<?php
require_once 'include/SQLWrapper.php';
require_once 'include/mail_config.php';
require_once 'lib/PHPMailerAutoload.php';
$db = new SQLWrapper();
if (isset($_POST['email']) && isset($_POST['password'])) {
    if ($db->getUser($_POST['email'], $_POST['password'])) {
        $db->deleteUser($_POST['email']);
        $mail = new PHPMailer();
        $mail->IsSMTP();
        $mail->SMTPAuth = TRUE;
        $mail->SMTPSecure = "tls";
        $mail->SMTPDebug = 2;
        $mail->Port = port;
        $mail->Username = mailUser;
        $mail->Password = mailPassword;
        $mail->Host = mailHost;
        $mail->Mailer = mailer;
        $mail->SetFrom(senderEmail, senderName);
        $mail->AddAddress($_POST['email']);
        $mail->Subject = "LiftLogger Account Delete";
        $mail->Body = "Your account has been deleted, sorry to see you go";
        echo "deleted";
    } else {
        echo "error";
    }
}