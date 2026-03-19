package com.example.birthdayapp

object HeartbeatConfig {
    /**
     * Change THIS_USER_ID before building for each device:
     *   - Build with "user_a" and install on YOUR phone
     *   - Build with "user_b" and install on HER phone
     */
    const val THIS_USER_ID = "user_a"
    val OTHER_USER_ID = if (THIS_USER_ID == "user_a") "user_b" else "user_a"

    // Display names shown in the notification body
    val SENDER_NAME = if (THIS_USER_ID == "user_a") "Nazar" else "Bahar"
}
