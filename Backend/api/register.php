<?php
require_once 'lib/PHPMailerAutoload.php';
require_once 'include/SQLWrapper.php';
require_once 'include/mail_config.php';
$db = new SQLWrapper();

$response = array("error" => FALSE);
 
if (isset($_POST['name']) && isset($_POST['email']) && isset($_POST['password'])) {
    $user = $db->checkUser($_POST['email']);
    if (!$user) {
        $user = $db->storeUser($_POST['email'], $_POST['password'], $_POST['name'], $_POST['dob'], $_POST['gender'], $_POST['height'], $_POST['weight'], $_POST['goal']);
        if ($user) {
            $response["message"] = "User registered";
            $response["user"] = $user;
            $emailBody = "Welcome to lift logger " . $_POST['name'] . " you have successfully been registered ";
            $mail = new PHPMailer();
            $mail->IsSMTP();
            $mail->SMTPAuth = TRUE;
            $mail->SMTPSecure = "tls";
            $mail->Port = port;
            $mail->Username = mailUser;
            $mail->Password = mailPassword;
            $mail->Host = mailHost;
            $mail->Mailer = mailer;
            $mail->SetFrom(senderEmail, senderName);
            $mail->AddAddress($_POST['email']);
            $mail->Subject = "Registration Complete";
            $mail->Body = $emailBody;
            $mail->IsHTML(false);
            $mail->send();
            echo json_encode($response);
        } else {
            $response["error"] = TRUE;
            $response["message"] = "Unknown Error occurred";
            echo json_encode($response);
        }
    } else {
        $response["error"] = TRUE;
        $response["message"] = "User already exists";
        echo json_encode($response);
    }
} else {
    $response["error"] = TRUE;
    $response["message"] = "Required fields missing";
    echo json_encode($response);
}