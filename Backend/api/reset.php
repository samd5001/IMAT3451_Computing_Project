<?php
require_once '../api/include/SQLWrapper.php';
require_once 'user.php';
if (isset($_POST['password']) && isset($_POST['passwordconfirm'])) {
    if ($_POST['password'] == $_POST['passwordconfirm']) {
        $password = $_POST['password'];
        $db = new SQLWrapper();
        $db->updatePassword(email, $password);
        unlink('reset_password.php');
        unlink('user.php');
        unlink(__FILE__);
        include '../api/templates/top.html';
        echo '<h2>Password Changed!</h2>';
        include '../api/templates/bottom.html';
    }
} else {
    include '../api/templates/top.html';
    echo '<form action="reset.php" method="post">
                <h2>Forgot your password?</h2>
                <p>Enter your email to be sent a reset password link</p>
                <label for="email">Email</label>
                <input id="email" type="email" name="email"/>
                <label for="password">Password</label>
                <input id="password" type="password" name="password"/>
                <label for="passwordconfirm">Confirm</label>
                <input id="passwordconfirm" type="password" name="passwordconfirm"/>
                <input type="submit"/>
                <h2>Passwords do not match!</h2>
            </form>';
    include '../api/templates/bottom.html';
}