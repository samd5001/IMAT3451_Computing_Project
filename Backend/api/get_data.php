<?php
require_once 'include/SQLWrapper.php';
$db = new SQLWrapper();
if (isset($_POST['email']) && isset($_POST['password'])) {
    if ($db->getUser($_POST['email'], $_POST['password'])) {
        $response['exercises'] = $db->getUserExercises($_POST['email']);
        $response['plans'] = $db->getUserPlans($_POST['email']);
        $response['days'] = $db->getUserDays($_POST['email']);
        $response['records'] = $db->getRecords($_POST['email']);
    } else {
        echo "Authentication error";
    }
}

echo json_encode($response);