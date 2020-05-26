<?php
require_once 'include/SQLWrapper.php';
$db = new SQLWrapper();


$response = array("error" => FALSE);

if (isset($_POST['email'])) {
    $email = $_POST['email'];
    if ($db->checkUser($email)) {
        // user already existed
        echo json_encode($response);
    } else {
        $response["error"] = TRUE;
        echo json_encode($response);
    }
}