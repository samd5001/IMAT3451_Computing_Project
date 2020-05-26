<?php
include 'api/templates/top.html';
echo   '<form action="reset_password.php" method="post">
            <h2>Forgot your password?</h2>
            <p>Enter your email to be sent a reset password link</p>
            <label for="email">Email</label>
            <input id="email" type="text" name="email"/>
            <input type="submit"/>
         <form/>';
include 'api/templates/bottom.html';