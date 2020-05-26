<?php
require_once 'api/include/SQLWrapper.php';
$db = new SQLWrapper();

if (isset($_POST['email'])) {
    $email = $_POST['email'];
    if ($db->checkUser($email)) {
        $url = 'http://86.1.54.2/api/mail_recovery.php';
        $data = array('email' => $email);
        $options = array(
            'http' => array(
                'header'  => "Content-type: application/x-www-form-urlencoded",
                'method'  => 'POST',
                'content' => http_build_query($data)
            )
        );
        $context  = stream_context_create($options);
        $result = file_get_contents($url, false, $context);
        $response = '<h2>A recovery email has been sent to ' . $email . '</h2>';
    } else {
        $response = '<form action="reset_password.php" method="post">
                <h2>Forgot your password?</h2>
                <p>Enter your email to be sent a reset password link</p>
                <label for="email">Email</label>
                <input id="email" type="text" name="email"/>
                <input type="submit"/>
                <h2>Email not registered, please try again</h2>';
    }
}
echo
'<!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="author" content="P14137775">
                <meta name=viewport content=\'width=240\'>
                <title>Lift Logger</title>
                <link rel="stylesheet" href="styles/styles.css">
                <link href="https://fonts.googleapis.com/css?family=Roboto:100" rel="stylesheet">
            </head>
            <body>
                <main>
                    <div id="container">
                        <h1>Lift Logger</h1>
                        ' . $response . '
                    </div>
                </main>
            </body>
            </html>';