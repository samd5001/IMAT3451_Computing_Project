<?php
require_once 'include/SQLWrapper.php';
$db = new SQLWrapper();
$response = $db->getPlans();
echo json_encode($response);