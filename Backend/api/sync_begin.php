<?php
require_once 'include/SQLWrapper.php';
$db = new SQLWrapper();
if (isset($_POST['email']) && (isset($_POST['password']))) {
    if ($db->getUser($_POST['email'], $_POST['password'])) {
        $response = $db->checkRowCounts($_POST['email']);
        $response['lastid'] = $db->getLastRecordID($_POST['email']);
        $response['error'] = false;
        echo json_encode($response);
    } else {
        $response['error'] = true;
        echo json_encode($response);
    }
}
