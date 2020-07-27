package com.likethesalad.android.buddy.modules.customconfig.utils

import com.google.auto.factory.AutoFactory
import com.likethesalad.android.buddy.modules.customconfig.data.ConfigurationGroup
import com.likethesalad.android.buddy.modules.customconfig.data.ConfigurationType
import com.likethesalad.android.common.utils.Constants

@AutoFactory
class ConfigurationNamesGenerator(
    private val configurationGroup: ConfigurationGroup,
    buildTypeName: String
) {
    private val capitalizedBuildTypeName = buildTypeName.capitalize()

    fun getBucketName(): String {
        return getCustomConfigurationName(configurationGroup.bucketType)
    }

    fun getConsumableName(): String {
        return getCustomConfigurationName(configurationGroup.consumableType)
    }

    fun getResolvableName(): String {
        return getCustomConfigurationName(configurationGroup.resolvableType)
    }

    private fun getCustomConfigurationName(configurationType: ConfigurationType): String {
        return "${Constants.CUSTOM_CONFIGURATIONS_PREFIX}$capitalizedBuildTypeName${configurationType.capitalizedName}"
    }
}