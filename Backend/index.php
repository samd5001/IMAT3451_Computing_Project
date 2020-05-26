<?php
include 'api/templates/top.html';
echo '<form action="getrecords.php" method="post">
    <h2>Welcome!</h2>
    <p>Enter your login details</p>
    <label for="email">Email</label>
    <input id="email" type="email" name="email"/>
    <label for="password">password</label>
    <input id="password" type="password" name="password"/>
    <input type="submit"/>
</form>
<br/>
 <a href="forgot.php">Forgot your password?</a>';

include 'api/templates/bottom.html';