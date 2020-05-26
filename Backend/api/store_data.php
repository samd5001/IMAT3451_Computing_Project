<?php
require_once 'include/SQLWrapper.php';
$db = new SQLWrapper();
if (isset($_POST['id'])) {
    $response = $db->storeRecord($_POST['email'], $_POST['id'], $_POST['exerciseName'], $_POST['planName'], $_POST['dayNumber'], $_POST['timeDone'], $_POST['sets']);
    echo ($response);
}
if (isset($_POST['name']) && (isset($_POST['type']))) {
    $response = $db->storeExercise($_POST['email'], $_POST['name'], $_POST['description'], $_POST['type'], $_POST['imageURL'], $_POST['minThreshold'], $_POST['maxThreshold'], $_POST['areasWorked'], $_POST['userMade']);
}