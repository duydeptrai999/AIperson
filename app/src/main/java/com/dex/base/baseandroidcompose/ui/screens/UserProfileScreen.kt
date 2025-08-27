package com.dex.base.baseandroidcompose.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dex.base.baseandroidcompose.R
import com.dex.base.baseandroidcompose.data.models.Location
import com.dex.base.baseandroidcompose.data.models.Occupation
import com.dex.base.baseandroidcompose.data.models.UserProfile
import com.dex.base.baseandroidcompose.data.models.WeatherPreferences
import com.dex.base.baseandroidcompose.ui.viewmodels.UserViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    onNavigateBack: () -> Unit = {},
    onProfileSaved: () -> Unit = {},
    userViewModel: UserViewModel = hiltViewModel()
) {
    val userProfile by userViewModel.userProfile.collectAsStateWithLifecycle()
    val isLoading by userViewModel.isLoading.collectAsStateWithLifecycle()
    val error by userViewModel.error.collectAsStateWithLifecycle()
    
    // State for edit mode with animation
    var isEditMode by remember { mutableStateOf(false) }
    
    // Form states
    var age by remember { mutableStateOf("") }
    var selectedOccupation by remember { mutableStateOf(Occupation.OFFICE_WORKER) }
    var location by remember { mutableStateOf("") }
    
    // Animation states
    val animatedVisibilityState = remember { MutableTransitionState(false) }
    animatedVisibilityState.targetState = true
    
    // Update form states when profile changes
    LaunchedEffect(userProfile) {
        userProfile?.let { profile ->
            age = profile.age.toString()
            selectedOccupation = profile.occupation
            location = profile.location.city
            isEditMode = false // Reset edit mode when profile loads
        }
    }
    
    // Determine if we have existing profile
    val hasExistingProfile = userProfile != null
    
    // Show edit form if no profile exists OR if in edit mode
    val showEditForm = !hasExistingProfile || isEditMode
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
    ) {
        AnimatedVisibility(
            visibleState = animatedVisibilityState,
            enter = fadeIn(animationSpec = tween(800)) + slideInVertically(
                animationSpec = tween(800),
                initialOffsetY = { it / 3 }
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
        if (showEditForm) {
            // Edit/Create Form
            ProfileEditForm(
                age = age,
                onAgeChange = { age = it },
                selectedOccupation = selectedOccupation,
                onOccupationChange = { selectedOccupation = it },
                location = location,
                onLocationChange = { location = it },
                hasExistingProfile = hasExistingProfile,
                isLoading = isLoading,
                onSave = {
                    val ageInt = age.toIntOrNull() ?: 25
                    val newProfile = UserProfile(
                        id = userProfile?.id ?: UUID.randomUUID().toString(),
                        age = ageInt,
                        location = Location(
                            city = location.ifBlank { "" },
                            country = "",
                            latitude = 0.0,
                            longitude = 0.0,
                            timezone = ""
                        ),
                        occupation = selectedOccupation,
                        preferences = userProfile?.preferences ?: WeatherPreferences(),
                        pointBalance = userProfile?.pointBalance ?: 0,
                        totalPointsEarned = userProfile?.totalPointsEarned ?: 0,
                        level = userProfile?.level ?: 1,
                        createdAt = userProfile?.createdAt ?: System.currentTimeMillis(),
                        lastUpdated = System.currentTimeMillis()
                    )
                    userViewModel.saveUserProfile(newProfile)
                    if (!hasExistingProfile) {
                        onProfileSaved()
                    }
                },
                onCancel = if (hasExistingProfile) {
                    { isEditMode = false }
                } else null
            )
        } else {
            // Profile View Mode
            ProfileViewMode(
                userProfile = userProfile!!,
                onEditClick = { isEditMode = true }
            )
        }
        
                // Error handling
                error?.let { errorMessage ->
                    Spacer(modifier = Modifier.height(20.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(16.dp)
                            ),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = errorMessage,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileViewMode(
    userProfile: UserProfile,
    onEditClick: () -> Unit
) {
    Column {
        // Header with Edit button
        ProfileViewHeader(
            onEditClick = onEditClick
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Profile Data Card
        ProfileDataCard(
            userProfile = userProfile
        )
    }
}

@Composable
fun ProfileViewHeader(
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(24.dp)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Enhanced Avatar with gradient
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary
                                )
                            )
                        )
                        .border(
                            width = 3.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(36.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(20.dp))
                
                Column {
                    Text(
                        text = stringResource(R.string.user_profile_view_title),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Manage your profile",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Enhanced Edit Button
            FilledTonalIconButton(
                onClick = onEditClick,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.edit),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}

@Composable
fun ProfileDataCard(
    userProfile: UserProfile
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(24.dp)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(28.dp)
        ) {
            ProfileInfoRow(
                icon = Icons.Default.Cake,
                label = stringResource(R.string.age),
                value = "${userProfile.age} years old",
                iconColor = MaterialTheme.colorScheme.primary
            )
            
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 20.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                thickness = 1.dp
            )
            
            ProfileInfoRow(
                icon = Icons.Default.Work,
                label = stringResource(R.string.occupation),
                value = userProfile.occupation.displayName,
                iconColor = MaterialTheme.colorScheme.secondary
            )
            
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 20.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                thickness = 1.dp
            )
            
            ProfileInfoRow(
                icon = Icons.Default.LocationOn,
                label = stringResource(R.string.location),
                value = if (userProfile.location.country.isNotBlank()) {
                    "${userProfile.location.city}, ${userProfile.location.country}"
                } else {
                    userProfile.location.city
                },
                iconColor = MaterialTheme.colorScheme.tertiary
            )
        }
    }
}

@Composable
fun ProfileInfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    iconColor: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon with background
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(iconColor.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp
            )
        }
    }
}

// Overloaded function for backward compatibility
@Composable
fun ProfileInfoRow(
    label: String,
    value: String
) {
    ProfileInfoRow(
        icon = Icons.Default.Info,
        label = label,
        value = value,
        iconColor = MaterialTheme.colorScheme.primary
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileEditForm(
    age: String,
    onAgeChange: (String) -> Unit,
    selectedOccupation: Occupation,
    onOccupationChange: (Occupation) -> Unit,
    location: String,
    onLocationChange: (String) -> Unit,
    hasExistingProfile: Boolean,
    isLoading: Boolean,
    onSave: () -> Unit,
    onCancel: (() -> Unit)? = null
) {
    var showOccupationDropdown by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(24.dp)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Form Header
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = if (hasExistingProfile) "Edit Profile" else "Create Profile",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            // Age Input with icon
            OutlinedTextField(
                value = age,
                onValueChange = onAgeChange,
                label = { Text(stringResource(R.string.age)) },
                placeholder = { Text(stringResource(R.string.enter_age)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Cake,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )
            
            // Occupation Dropdown with icon
            ExposedDropdownMenuBox(
                expanded = showOccupationDropdown,
                onExpandedChange = { showOccupationDropdown = it }
            ) {
                OutlinedTextField(
                    value = selectedOccupation.displayName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.occupation)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Work,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showOccupationDropdown) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(16.dp)
                )
                
                ExposedDropdownMenu(
                    expanded = showOccupationDropdown,
                    onDismissRequest = { showOccupationDropdown = false }
                ) {
                    Occupation.values().forEach { occupation ->
                        DropdownMenuItem(
                            text = { Text(occupation.displayName) },
                            onClick = {
                                onOccupationChange(occupation)
                                showOccupationDropdown = false
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Work,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        )
                    }
                }
            }
            
            // Location Input with icon
            OutlinedTextField(
                value = location,
                onValueChange = onLocationChange,
                label = { Text(stringResource(R.string.location)) },
                placeholder = { Text(stringResource(R.string.enter_location)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )
            
            // Action Buttons with modern design
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = if (onCancel != null) Arrangement.spacedBy(16.dp) else Arrangement.Center
            ) {
                // Cancel Button (only show if editing existing profile)
                onCancel?.let {
                    OutlinedButton(
                        onClick = it,
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        enabled = !isLoading,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            stringResource(R.string.cancel),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                
                // Save/Update Button
                Button(
                    onClick = onSave,
                    modifier = if (onCancel != null) Modifier.weight(1f).height(56.dp) else Modifier.fillMaxWidth().height(56.dp),
                    enabled = !isLoading && age.isNotBlank() && location.isNotBlank(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Icon(
                            imageVector = if (hasExistingProfile) Icons.Default.Update else Icons.Default.Save,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            stringResource(
                                if (hasExistingProfile) R.string.update_profile 
                                else R.string.save_profile
                            ),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}