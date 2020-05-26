<?php
    include 'api/include/SQLWrapper.php';

    $db = new SQLWrapper();
    $user = $db->getUser($_POST['email'], $_POST['password']);
    $records = null;
    if($user) {
        $records = $db->getRecords($_POST['email']);
    }


    include 'api/templates/top.html';
    echo
    '<h2>Records</h2>
        <p>The following is a list of all records stored in your account</p>
        <ul class="recordList">';

    foreach($records as $record) {
        echo '<li><div class="record"><h3>' . $record['exerciseName'] . '</h3><h4>' . $record['timeDone'] . '</h4><table>';
        $sets = $record['sets'];
        echo '<tr><th>Reps</th><th>Weight (kg)</th></tr>';
        foreach ($sets as $set) {
            echo ' <tr><td>' . $set->reps . '</td><td>' . $set->weight . '</td></tr>';
        }
        echo '</table></div></li><br/>';
    }

        echo '</ul>';

    include 'api/templates/bottom.html';