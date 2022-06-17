package com.kauel.shippingmark.utils

import androidx.lifecycle.LifecycleCoroutineScope
import androidx.navigation.NavController

fun NavController.lifeCycleNavigate(lifecycle : LifecycleCoroutineScope, resId :Int) =
    lifecycle.launchWhenResumed {
        navigate(resId)
    }