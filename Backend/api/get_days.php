<?php
require_once 'include/SQLWrapper.php';
$db = new SQLWrapper();
$response = $db->getDays();
echo json_encode($response);