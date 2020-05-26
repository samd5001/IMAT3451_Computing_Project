<?php
require_once 'include/SQLWrapper.php';
$db = new SQLWrapper();
if (isset($_POST['exerciseName'])) {
    $db->deleteExercise($_POST['email'], $_POST['exerciseName']);
}

if (isset($_POST['id'])) {
    $db->deleteRecord($_POST['email'], $_POST['id']);
}