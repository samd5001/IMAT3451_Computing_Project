<?php
require_once 'include/SQLWrapper.php';
$db = new SQLWrapper();
$response['exercises'] = $db->getExercises();
$response['plans'] = $db->getPlans();
$response['days'] = $db->getDays();
echo json_encode($response);