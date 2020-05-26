<?php
include '../api/templates/top.html';

echo '    
<form action="reset.php" method="post">
    <h2>Forgot your password?</h2>
    <p>Enter your new password</p>
    <label for="password">Password</label>
    <input id="password"  pattern="(?=.*\d)(?=.*[a-z])(?=.*[?~!@-_#$%£]).{8,16}" title="Must contain at least one number, one character and one special character (*?~!@-_#$%£)" type="password" name="password"/>
    <label for="passwordconfirm">Confirm</label>
    <input id="passwordconfirm" type="password" name="passwordconfirm"/>
    <input type="submit"/>
</form>';

include '../api/templates/bottom.html';
