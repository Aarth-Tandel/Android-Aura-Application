package com.wozart.aura.aura.model;

/***************************************************************************
 * File Name : AwsReported
 * Author : Aarth Tandel
 * Date of Creation : 29/12/17
 * Description : Data model for AWS IOT reported State
 * Revision History :
 * ____________________________________________________________________________
 * 29/12/17  Aarth Tandel - Initial Commit
 * ____________________________________________________________________________
 * 29/12/17 Version 1.0
 * ____________________________________________________________________________
 *
 *****************************************************************************/

public class AwsReported {
    public AwsState desired;
    public AwsState reported;
}
