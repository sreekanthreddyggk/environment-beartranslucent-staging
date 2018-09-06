pipeline 
{
    agent 
	{
      label "jenkins-maven"
    }
    environment 
	{
      ORG               = 'gitea'
      APP_NAME          = 'environment-hissertruth-staging'
      CHARTMUSEUM_CREDS = credentials('jenkins-x-chartmuseum')
    }
    stages 
	{
      stage('CI Build and push snapshot') 
	  {
        when 
		{
          branch 'PR-*'
        }
        environment 
		{
          PREVIEW_VERSION = "0.0.0-SNAPSHOT-$BRANCH_NAME-$BUILD_NUMBER"
          PREVIEW_NAMESPACE = "$APP_NAME-$BRANCH_NAME".toLowerCase()
          HELM_RELEASE = "$PREVIEW_NAMESPACE".toLowerCase()
        }
        steps 
		{
          container('maven') 
		  {
            sh "mvn versions:set -DnewVersion=$PREVIEW_VERSION"
            sh "mvn install"
            //sh 'export VERSION=$PREVIEW_VERSION && skaffold build -f skaffold.yaml'


            //sh "jx step post build --image $DOCKER_REGISTRY/$ORG/$APP_NAME:$PREVIEW_VERSION"
          }
        }
      }
	}
}
