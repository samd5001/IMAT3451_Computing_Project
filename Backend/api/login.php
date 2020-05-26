<?php

require_once 'include/SQLWrapper.php';
$db = new SQLWrapper();

$response = array("error" => FALSE);
 
if (isset($_POST['email']) && isset($_POST['password'])) {

    $email = $_POST['email'];
    $password = $_POST['password'];

    $user = $db->getUser($email, $password);
 
    if ($user != false) {
        $response['user'] = $user;

        echo json_encode($response);
    } else {
        $response["error"] = TRUE;
        $response["message"] = "User not found, check your password and email";
        echo json_encode($response);
    }
} else {
    $response["error"] = TRUE;
    $response["message"] = "Please enter a valid email and password";
    echo json_encode($response);
}
