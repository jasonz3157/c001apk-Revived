package com.example.c001apk.compose.ui.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.example.c001apk.compose.FollowType
import com.example.c001apk.compose.ThemeMode
import com.example.c001apk.compose.ThemeType
import com.example.c001apk.compose.UiStyle
import com.example.c001apk.compose.logic.model.HapticStrength
import com.example.c001apk.compose.logic.repository.UserPreferencesRepository
import com.example.c001apk.compose.ui.base.PrefsViewModel
import com.example.c001apk.compose.util.LauncherIcon
import com.example.c001apk.compose.util.LauncherIconManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Created by bggRGjQaUbCoE on 2024/5/31
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val launcherIconManager: LauncherIconManager,
    private val deviceIdentity: com.example.c001apk.compose.logic.repository.DeviceIdentityRepository,
) : PrefsViewModel(userPreferencesRepository) {
    var keywordSaveError by mutableStateOf<String?>(null)
        private set

    fun saveBlockedKeywords(value: String, onSaved: () -> Unit) {
        keywordSaveError = null
        viewModelScope.launch {
            try {
                val normalized = com.example.c001apk.compose.util.KeywordFilter.parse(value).joinToString("\n")
                userPreferencesRepository.setBlockedKeywords(normalized)
                onSaved()
            } catch (e: kotlinx.coroutines.CancellationException) { throw e }
            catch (_: Exception) { keywordSaveError = "保存失败，请重试" }
        }
    }

    var acquiringDeviceId by mutableStateOf(false)
        private set
    var deviceIdMessage by mutableStateOf<String?>(null)
        private set

    fun acquireDeviceId() {
        if (acquiringDeviceId) return
        acquiringDeviceId = true
        deviceIdMessage = null
        viewModelScope.launch {
            try {
                deviceIdentity.acquire()
                deviceIdMessage = "已自动获取并保存"
            } catch (e: kotlinx.coroutines.CancellationException) { throw e }
            catch (e: Exception) {
                deviceIdMessage = if (e is IllegalStateException) e.message else "获取失败，请检查网络或手动输入"
            } finally { acquiringDeviceId = false }
        }
    }

    fun saveDeviceId(text: String, onSaved: () -> Unit) {
        viewModelScope.launch {
            try { deviceIdentity.save(text); onSaved() }
            catch (e: kotlinx.coroutines.CancellationException) { throw e }
            catch (e: Exception) { deviceIdMessage = "保存失败，请检查 ID 后重试" }
        }
    }

    fun setBlurScope(value: com.example.c001apk.compose.BlurScope) {
        viewModelScope.launch { userPreferencesRepository.setBlurScope(value) }
    }

    fun setBlurEnabled(value: Boolean) {
        viewModelScope.launch { userPreferencesRepository.setBlurEnabled(value) }
    }

    fun setUiStyle(value: UiStyle) {
        viewModelScope.launch { userPreferencesRepository.setUiStyle(value) }
    }

    var launcherIcon by mutableStateOf(launcherIconManager.current)
        private set
    var isChangingIcon by mutableStateOf(false)
        private set

    fun setLauncherIcon(icon: LauncherIcon, onResult: (Boolean) -> Unit) {
        if (isChangingIcon) return
        isChangingIcon = true
        viewModelScope.launch {
            try {
                val changed = withContext(Dispatchers.IO) {
                    runCatching { launcherIconManager.select(icon) }.isSuccess
                }
                launcherIcon = launcherIconManager.current
                onResult(changed)
            } finally {
                isChangingIcon = false
            }
        }
    }

    fun setDarkTheme(value: ThemeMode) {
        viewModelScope.launch {
            userPreferencesRepository.setThemeMode(value)
        }
    }

    fun setMaterialYou(value: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setMaterialYou(value)
        }
    }

    fun setPureBlack(value: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setPureBlack(value)
        }
    }

    fun setFontScale(value: Float) {
        viewModelScope.launch {
            userPreferencesRepository.setFontScale(value)
        }
    }

    fun setContentScale(value: Float) {
        viewModelScope.launch {
            userPreferencesRepository.setContentScale(value)
        }
    }

    fun setSZLMId(value: String) {
        viewModelScope.launch {
            userPreferencesRepository.setSZLMId(value)
        }
    }

    fun setImageQuality(value: Int) {
        viewModelScope.launch {
            userPreferencesRepository.setImageQuality(value)
        }
    }

    fun setImageFilter(value: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setImageFilter(value)
        }
    }

    fun setOpenInBrowser(value: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setOpenInBrowser(value)
        }
    }

    fun setShowSquare(value: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setShowSquare(value)
        }
    }

    fun setRecordHistory(value: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setRecordHistory(value)
        }
    }

    fun setShowEmoji(value: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setShowEmoji(value)
        }
    }

    fun setCheckUpdate(value: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setCheckUpdate(value)
        }
    }

    fun setCheckCount(value: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setCheckCount(value)
        }
    }

    fun setVersionName(value: String) {
        viewModelScope.launch {
            userPreferencesRepository.setVersionName(value)
        }
    }

    fun setApiVersion(value: String) {
        viewModelScope.launch {
            userPreferencesRepository.setApiVersion(value)
        }
    }

    fun setVersionCode(value: String) {
        viewModelScope.launch {
            userPreferencesRepository.setVersionCode(value)
        }
    }

    fun setManufacturer(value: String) {
        viewModelScope.launch {
            userPreferencesRepository.setManufacturer(value)
        }
    }

    fun setBrand(value: String) {
        viewModelScope.launch {
            userPreferencesRepository.setBrand(value)
        }
    }

    fun setModel(value: String) {
        viewModelScope.launch {
            userPreferencesRepository.setModel(value)
        }
    }

    fun setBuildNumber(value: String) {
        viewModelScope.launch {
            userPreferencesRepository.setBuildNumber(value)
        }
    }

    fun setSdkInt(value: String) {
        viewModelScope.launch {
            userPreferencesRepository.setSdkInt(value)
        }
    }

    fun setAndroidVersion(value: String) {
        viewModelScope.launch {
            userPreferencesRepository.setAndroidVersion(value)
        }
    }

    fun setUserAgent(value: String) {
        viewModelScope.launch {
            userPreferencesRepository.setUserAgent(value)
        }
    }

    fun setXAppDevice(value: String) {
        viewModelScope.launch {
            userPreferencesRepository.setXAppDevice(value)
        }
    }

    fun setXAppToken(value: String) {
        viewModelScope.launch {
            userPreferencesRepository.setXAppToken(value)
        }
    }

    fun setFollowType(value: FollowType) {
        viewModelScope.launch {
            userPreferencesRepository.setFollowType(value)
        }
    }

    fun setThemeType(value: ThemeType) {
        viewModelScope.launch {
            userPreferencesRepository.setThemeType(value)
        }
    }

    fun setSeedColor(value: String) {
        viewModelScope.launch {
            userPreferencesRepository.setSeedColor(value)
        }
    }

    fun setPaletteStyle(value: Int) {
        viewModelScope.launch {
            userPreferencesRepository.setPaletteStyle(value)
        }
    }

    fun setHapticFeedback(value: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setHapticFeedback(value)
        }
    }

    fun setHapticStrength(value: HapticStrength) {
        viewModelScope.launch {
            userPreferencesRepository.setHapticStrength(value)
        }
    }

    fun setCustomHaptics(value: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setCustomHaptics(value)
        }
    }

    fun setReverseHapticEffects(value: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setReverseHapticEffects(value)
        }
    }

}
