node('nix') {
    stage('build') {
        checkout scm
        sh 'nix-shell -p sbt --command "sbt test"'
    }

    stage('publish') {
        if (env.BRANCH_NAME == 'master' ) {
            withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'ephox-bintray', usernameVariable: 'BINTRAY_USER', passwordVariable: 'BINTRAY_PASS']]) {
                sh 'nix-shell -p sbt --command "sbt publish"'
            }
        } else {
            echo "Not master branch - don't publish."
        }
    }
}
