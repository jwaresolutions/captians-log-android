package com.captainslog.ui.license

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.captainslog.viewmodel.LicenseProgressViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * Screen displaying captain's license progress tracking.
 * Shows sea time days, progress toward goals, and estimated completion dates.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LicenseProgressScreen(
    modifier: Modifier = Modifier
) {
    // Create ViewModel with Context dependency
    val context = androidx.compose.ui.platform.LocalContext.current
    val viewModel: LicenseProgressViewModel = remember { 
        LicenseProgressViewModel(context) 
    }
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadLicenseProgress()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            uiState.error != null -> {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Error Loading Progress",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Text(
                            text = uiState.error ?: "Unknown error",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Button(
                            onClick = { viewModel.loadLicenseProgress() }
                        ) {
                            Text("Retry")
                        }
                    }
                }
            }
            
            uiState.progress != null -> {
                val progress = uiState.progress!!
                
                // Overview Card
                OverviewCard(progress = progress)
                
                // 360-Day Goal Card
                GoalCard(
                    title = "360-Day Total Goal",
                    description = "Total sea time days for 6-pack OUPV license",
                    currentDays = progress.totalDays,
                    goalDays = 360,
                    estimatedCompletion = progress.estimatedCompletion360,
                    progressColor = MaterialTheme.colorScheme.primary
                )
                
                // 90-Day in 3 Years Goal Card
                GoalCard(
                    title = "90 Days in 3 Years Goal",
                    description = "Recent sea time for license renewal",
                    currentDays = progress.daysInLast3Years,
                    goalDays = 90,
                    estimatedCompletion = progress.estimatedCompletion90In3Years,
                    progressColor = MaterialTheme.colorScheme.secondary
                )
                
                // Activity Rate Card
                ActivityRateCard(progress = progress)
                
                // Information Card
                InformationCard()
            }
        }
    }
}

@Composable
fun OverviewCard(
    progress: com.captainslog.data.LicenseProgress,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Sea Time Overview",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatisticColumn(
                    label = "Total Days",
                    value = progress.totalDays.toString(),
                    subtitle = "${String.format("%.1f", progress.totalHours)} hours"
                )
                
                StatisticColumn(
                    label = "Last 3 Years",
                    value = progress.daysInLast3Years.toString(),
                    subtitle = "${String.format("%.1f", progress.hoursInLast3Years)} hours"
                )
            }
        }
    }
}

@Composable
fun GoalCard(
    title: String,
    description: String,
    currentDays: Int,
    goalDays: Int,
    estimatedCompletion: String?,
    progressColor: Color,
    modifier: Modifier = Modifier
) {
    val progress = (currentDays.toFloat() / goalDays.toFloat()).coerceAtMost(1.0f)
    val remainingDays = (goalDays - currentDays).coerceAtLeast(0)
    
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Progress bar
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "$currentDays / $goalDays days",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = progressColor
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = progressColor
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Status and estimates
            if (currentDays >= goalDays) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🎉 Goal Completed!",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Remaining: $remainingDays days",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    
                    if (estimatedCompletion != null) {
                        Text(
                            text = "Estimated completion: $estimatedCompletion",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Text(
                            text = "Start logging trips to see completion estimate",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ActivityRateCard(
    progress: com.captainslog.data.LicenseProgress,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Activity Rate",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatisticColumn(
                    label = "Average",
                    value = String.format("%.1f", progress.averageDaysPerMonth),
                    subtitle = "days/month"
                )
                
                StatisticColumn(
                    label = "Yearly Rate",
                    value = String.format("%.0f", progress.averageDaysPerMonth * 12),
                    subtitle = "days/year"
                )
            }
        }
    }
}

@Composable
fun InformationCard(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                Icons.Default.Info,
                contentDescription = "Information",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(end = 12.dp)
            )
            
            Column {
                Text(
                    text = "About Sea Time Calculation",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "• A sea time day requires 4+ hours as captain\n" +
                          "• Multi-day trips count as separate days\n" +
                          "• Same-day trips are aggregated together\n" +
                          "• Only trips where you were captain count\n" +
                          "• Progress is calculated across all boats",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun StatisticColumn(
    label: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}