<?php
require_once 'lib/PHPMailerAutoload.php';
require_once("include/mail_config.php");

$email = $_POST['email'];
$emailkey = substr($email, 0, 5);
$key = sha1(rand(1, 10));
$key = substr($key, 0, 5);
$path = "/" . $emailkey . $key . "/";

$mail = new PHPMailer();

$emailBody = "Please follow the link provided to reset your password http://86.1.54.2" . $path . "reset_password.php";

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
$mail->AddAddress($email);
$mail->Subject = "LiftLogger Password Reset";
$mail->Body = $emailBody;
$mail->IsHTML(false);

if(!$mail->Send())
    echo "Problem sending email." . $mail->ErrorInfo;
else
    echo "email sent.";

$path = ".." . $path;
mkdir($path, 0777, true);
$file = fopen($path."user.php", "w");
fwrite($file, "<?php define('email', '" . $email . "');");
fclose($file);
$file = 'reset.php';
copy($file, $path . $file);
$file = 'reset_password.php';
copy($file, $path . $file);



