<?php
require_once 'include/SQLWrapper.php';
$db = new SQLWrapper();
if (isset($_POST['email']) && isset($_POST['password'])) {
    if ($db->getUser($_POST['email'], $_POST['password'])) {
        $db->resetUser($_POST['email']);
    } else echo "User reset";
}