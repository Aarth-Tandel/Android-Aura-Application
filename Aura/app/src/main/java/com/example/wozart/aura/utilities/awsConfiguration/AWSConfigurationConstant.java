package com.example.wozart.aura.utilities.awsConfiguration;

import com.amazonaws.regions.Regions;

/**
 * Created by wozart on 29/12/17.
 */

public class AWSConfigurationConstant {
    public static final String AWS_MOBILEHUB_USER_AGENT =
            "MobileHub 1f6e558f-3db6-4386-89f0-3ebfa7f5e5f8 aws-my-sample-app-android-v0.18";
    // AMAZON COGNITO
    public static final Regions AMAZON_COGNITO_REGION =
            Regions.fromName("us-east-1");
    public static final String  AMAZON_COGNITO_IDENTITY_POOL_ID =
            "us-east-1:75e81819-c998-4fad-b1d4-9afd097ff5cf";
    public static final Regions AMAZON_DYNAMODB_REGION =
            Regions.fromName("us-east-1");

    private static final AWSMobileHelperConfiguration helperConfiguration = new AWSMobileHelperConfiguration.Builder()
            .withCognitoRegion(AMAZON_COGNITO_REGION)
            .withCognitoIdentityPoolId(AMAZON_COGNITO_IDENTITY_POOL_ID)
            .build();
    /**
     * @return the configuration for AWSKit.
     */
    public static AWSMobileHelperConfiguration getAWSMobileHelperConfiguration() {
        return helperConfiguration;
    }
}
