#!groovy
def BN = BRANCH_NAME == "master" || BRANCH_NAME.startsWith("releases/") ? BRANCH_NAME : "master"

library "knime-pipeline@$BN"

properties([
    // provide a list of upstream jobs which should trigger a rebuild of this job
    pipelineTriggers([
        upstream('knime-filehandling/' + env.BRANCH_NAME.replaceAll('/', '%2F'))
	]),
    buildDiscarder(logRotator(numToKeepStr: '5')),
    disableConcurrentBuilds()
])

try {
	// provide the name of the update site project
	knimetools.defaultTychoBuild('org.knime.update.python', 'maven && python2 && python3')

    
    withEnv([ "KNIME_POSTGRES_USER=knime01", "KNIME_POSTGRES_PASSWORD=password",
              "KNIME_MYSQL_USER=root", "KNIME_MYSQL_PASSWORD=password",
              "KNIME_MSSQLSERVER_USER=sa", "KNIME_MSSQLSERVER_PASSWORD=@SaPassword123"
    ]) {
    	workflowTests.runTests(
            dependencies: [
                repositories: [
                    'knime-chemistry',
                    'knime-database',
                    'knime-datageneration',
                    'knime-distance',
                    'knime-ensembles',
                    'knime-filehandling',
                    'knime-jep',
                    'knime-jfreechart',
                    'knime-js-base',
                    'knime-kerberos',
                    'knime-python',
                    'knime-testing-internal',
                    'knime-xml'
                ]
            ],
            sidecarContainers: [
                [ image: "${dockerTools.ECR}/knime/postgres:12", namePrefix: "POSTGRES", port: 5432, 
                    envArgs: [
                        "POSTGRES_USER=${env.KNIME_POSTGRES_USER}", "POSTGRES_PASSWORD=${env.KNIME_POSTGRES_PASSWORD}",
                        "POSTGRES_DB=knime_testing"
                    ]
                ],
                [ image: "${dockerTools.ECR}/knime/mysql5", namePrefix: "MYSQL", port: 3306, 
                    envArgs: ["MYSQL_ROOT_PASSWORD=${env.KNIME_MYSQL_PASSWORD}"]
                ],
                [ image: "${dockerTools.ECR}/knime/mssql-server", namePrefix: "MSSQLSERVER", port: 1433, 
                    envArgs: ["ACCEPT_EULA=Y", "SA_PASSWORD=${env.KNIME_MSSQLSERVER_PASSWORD}", "MSSQL_DB=knime_testing"]
                ]                
            ]
    	)
    }

	stage('Sonarqube analysis') {
		env.lastStage = env.STAGE_NAME
		workflowTests.runSonar()
	}
 } catch (ex) {
	 currentBuild.result = 'FAILURE'
	 throw ex
 } finally {
	 notifications.notifyBuild(currentBuild.result);
 }
/* vim: set shiftwidth=4 expandtab smarttab: */
