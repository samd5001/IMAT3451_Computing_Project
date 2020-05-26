<?php
require_once 'include/SQLWrapper.php';
$db = new SQLWrapper();
if (isset($_POST['email']) && (isset($_POST['password']))) {
    $response['error'] = false;
    $email = ($_POST['email']);
    if (isset($_POST['exercises'])) {
        if ($_POST['exercises'] < 0) {
            $response['exercises'] = $db->getUserExercisesName($email);
        } else {
            $response['exercises'] = $db->getUserExercises($email);
        }
    } else {
        $response['exercises'] = NULL;
    }

    if (isset($_POST['plans'])) {
        if ($_POST['plans'] < 0) {
            $response['plans'] = $db->getUserPlansName($email);
            $response['days'] = $db->getUserDays($email);
        } else {
            $response['plans'] = $db->getUserExercises($email);
            $response['days'] = NULL;
        }
    } else {
        $response['plans'] = NULL;
        $response['days'] = NULL;
    }

    if (isset($_POST['records'])) {
        $response['recordIDs'] = $db->getRecordIDs($email);
    } else {
        $response['recordIDs'] = NULL;
    }

    if (isset($_POST['lastid'])) {
        $response['records'] = $db->getNewRecords($email, $_POST['lastid']);
    } else {
        $response['records'] = NULL;
    }

    echo json_encode($response);

} else
{
    $response['error'] = true;
    echo $response;
}