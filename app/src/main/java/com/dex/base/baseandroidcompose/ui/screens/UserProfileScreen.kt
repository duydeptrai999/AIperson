package com.dex.base.baseandroidcompose.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dex.base.baseandroidcompose.R
import com.dex.base.baseandroidcompose.data.models.*
import com.dex.base.baseandroidcompose.ui.theme.*
import com.dex.base.baseandroidcompose.ui.viewmodels.WeatherViewModel
import com.dex.base.baseandroidcompose.ui.viewmodels.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    onNavigateBack: () -> Unit = {},
    onProfileSaved: () -> Unit = {},
    weatherViewModel: WeatherViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel()
) {
    var age by remember { mutableStateOf("") }
    var occupation by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    
    val userProfile by userViewModel.userProfile.collectAsState()
    val isLoading by userViewModel.isLoading.collectAsState()
    val error by userViewModel.error.collectAsState()
    
    // Load existing profile data if available
    LaunchedEffect(userProfile) {
        userProfile?.let { profile ->
            age = profile.age.toString()
            occupation = profile.occupation.name
            location = "${profile.location.city}, ${profile.location.country}"
        }
    }
    
    // Handle error messages
    LaunchedEffect(error) {
        error?.let {
            errorMessage = it
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        SkyBlue.copy(alpha = 0.3f),
                        CloudWhite
                    )
                )
            )
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.user_profile_title),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Profile Header
            ProfileHeader()
            
            // Age Input
            ProfileInputField(
                label = stringResource(R.string.age),
                value = age,
                onValueChange = { age = it },
                placeholder = stringResource(R.string.enter_age),
                icon = Icons.Default.Person,
                keyboardType = KeyboardType.Number
            )
            
            // Occupation Dropdown
            var expanded by remember { mutableStateOf(false) }
            val occupations = Occupation.values().map { it.name.replace("_", " ").lowercase().replaceFirstChar { char -> char.uppercase() } }
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = WeatherCardBackground
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = DeepSkyBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.occupation),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = occupation,
                            onValueChange = {},
                            readOnly = true,
                            placeholder = {
                                Text(
                                    text = stringResource(R.string.enter_occupation),
                                    color = RainyGray
                                )
                            },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = DeepSkyBlue,
                                unfocusedBorderColor = RainyGray.copy(alpha = 0.5f),
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            occupations.forEach { occupationOption ->
                                DropdownMenuItem(
                                    text = { Text(occupationOption) },
                                    onClick = {
                                        occupation = occupationOption
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
            
            // Location Input
            ProfileInputField(
                label = stringResource(R.string.location),
                value = location,
                onValueChange = { location = it },
                placeholder = stringResource(R.string.enter_location),
                icon = Icons.Default.LocationOn
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Save Button
            Button(
                onClick = {
                    // Validation
                    var isValid = true
                    var errorMessage = ""
                    
                    // Validate age
                    val ageInt = age.toIntOrNull()
                    if (ageInt == null || ageInt < 1 || ageInt > 120) {
                        isValid = false
                        errorMessage = "Please enter a valid age (1-120)"
                    }
                    
                    // Validate occupation
                    if (occupation.isBlank()) {
                        isValid = false
                        errorMessage = "Please select an occupation"
                    }
                    
                    // Validate location
                    if (location.isBlank()) {
                        isValid = false
                        errorMessage = "Location is required"
                    }
                    
                    if (isValid) {
                         errorMessage = "" // Clear error message
                         try {
                            val occupationEnum = try {
                                Occupation.valueOf(occupation.uppercase().replace(" ", "_"))
                            } catch (e: IllegalArgumentException) {
                                Occupation.OTHER
                            }
                            
                            // Parse location (assuming format "City, Country")
                            val locationParts = location.split(",")
                            val city = locationParts.getOrNull(0)?.trim() ?: "Unknown"
                            val country = locationParts.getOrNull(1)?.trim() ?: "Unknown"
                            
                            val newProfile = UserProfile(
                                id = userProfile?.id ?: java.util.UUID.randomUUID().toString(),
                                age = ageInt!!,
                                location = Location(
                                    city = city,
                                    country = country,
                                    latitude = 0.0, // Will be updated later with geocoding
                                    longitude = 0.0,
                                    timezone = "Asia/Ho_Chi_Minh" // Default timezone
                                ),
                                occupation = occupationEnum,
                                preferences = userProfile?.preferences ?: WeatherPreferences(),
                                pointBalance = userProfile?.pointBalance ?: 0,
                                totalPointsEarned = userProfile?.totalPointsEarned ?: 0,
                                level = userProfile?.level ?: 1,
                                createdAt = userProfile?.createdAt ?: System.currentTimeMillis(),
                                lastUpdated = System.currentTimeMillis()
                            )
                            
                            userViewModel.saveUserProfile(newProfile)
                            onProfileSaved()
                            
                        } catch (e: Exception) {
                            // Handle invalid occupation or other errors
                            // For now, use default values
                            val newProfile = UserProfile(
                                id = userProfile?.id ?: java.util.UUID.randomUUID().toString(),
                                age = age.toIntOrNull() ?: 25,
                                location = Location(
                                    city = location,
                                    country = "Unknown",
                                    latitude = 0.0,
                                    longitude = 0.0,
                                    timezone = "Asia/Ho_Chi_Minh" // Default timezone
                                ),
                                occupation = Occupation.OTHER,
                                preferences = userProfile?.preferences ?: WeatherPreferences(),
                                pointBalance = userProfile?.pointBalance ?: 0,
                                totalPointsEarned = userProfile?.totalPointsEarned ?: 0,
                                level = userProfile?.level ?: 1,
                                createdAt = userProfile?.createdAt ?: System.currentTimeMillis(),
                                lastUpdated = System.currentTimeMillis()
                            )
                            
                            userViewModel.saveUserProfile(newProfile)
                            onProfileSaved()
                        }
                    } else {
                         errorMessage = errorMessage
                     }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DeepSkyBlue
                ),
                enabled = !isLoading && age.isNotBlank() && occupation.isNotBlank() && location.isNotBlank()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Text(
                        text = stringResource(R.string.save_profile),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
            
            // Error message display
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            

        }
    }
}

@Composable
fun ProfileHeader() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = WeatherCardBackground
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Avatar
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                DeepSkyBlue.copy(alpha = 0.8f),
                                SkyBlue.copy(alpha = 0.6f)
                            )
                        ),
                        shape = RoundedCornerShape(40.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Personalize Your Weather Experience",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Help us provide better weather recommendations by sharing some basic information about yourself.",
                style = MaterialTheme.typography.bodyMedium,
                color = RainyGray
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = WeatherCardBackground
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = DeepSkyBlue,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = {
                    Text(
                        text = placeholder,
                        color = RainyGray
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DeepSkyBlue,
                    unfocusedBorderColor = RainyGray.copy(alpha = 0.5f),
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                ),
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                singleLine = true
            )
        }
    }
}