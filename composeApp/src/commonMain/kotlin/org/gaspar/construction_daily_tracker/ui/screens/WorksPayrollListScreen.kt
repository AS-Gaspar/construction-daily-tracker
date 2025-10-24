package org.gaspar.construction_daily_tracker.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.gaspar.construction_daily_tracker.i18n.Strings
import org.gaspar.construction_daily_tracker.model.Employee
import org.gaspar.construction_daily_tracker.model.Work
import org.gaspar.construction_daily_tracker.model.MonthlyPayroll

// Tailwind blue-600
private val TailwindBlue = Color(0xFF2563EB)
// Tailwind green-600
private val TailwindGreen = Color(0xFF16A34A)

/**
 * Data class to hold work payroll summary
 */
data class WorkPayrollSummary(
    val work: Work,
    val employeeCount: Int,
    val totalPayment: Double
)

/**
 * Screen showing payroll grouped by works for a specific period
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorksPayrollListScreen(
    strings: Strings,
    periodStartDate: String,
    periodEndDate: String,
    payrolls: List<MonthlyPayroll>,
    employees: List<Employee>,
    works: List<Work>,
    isLoading: Boolean,
    onBack: () -> Unit,
    onWorkClick: (Int) -> Unit
) {
    // Filter payrolls for this period and group by work
    val workPayrollSummaries = remember(payrolls, employees, works, periodStartDate, periodEndDate) {
        val periodPayrolls = payrolls.filter {
            it.periodStartDate == periodStartDate && it.periodEndDate == periodEndDate
        }

        // Group employees by work
        val workMap = mutableMapOf<Int?, MutableList<Pair<Employee, MonthlyPayroll>>>()

        periodPayrolls.forEach { payroll ->
            val employee = employees.find { it.id == payroll.employeeId }
            if (employee != null) {
                val workId = employee.workId
                if (!workMap.containsKey(workId)) {
                    workMap[workId] = mutableListOf()
                }
                workMap[workId]!!.add(Pair(employee, payroll))
            }
        }

        // Create summaries for works (excluding null workId)
        workMap.filter { it.key != null }
            .mapNotNull { (workId, employeePayrolls) ->
                val work = works.find { it.id == workId }
                if (work != null) {
                    val totalPayment = employeePayrolls.sumOf {
                        it.second.totalPayment.toDoubleOrNull() ?: 0.0
                    }
                    WorkPayrollSummary(
                        work = work,
                        employeeCount = employeePayrolls.size,
                        totalPayment = totalPayment
                    )
                } else null
            }
            .sortedBy { it.work.name.lowercase() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(strings.payrollByWorks)
                        Text(
                            text = formatPeriodLabel(strings, periodStartDate, periodEndDate),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = strings.back
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TailwindBlue,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = TailwindBlue
                    )
                }
                workPayrollSummaries.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "📊",
                            style = MaterialTheme.typography.displayLarge
                        )
                        Text(
                            text = strings.noWorksInPayroll,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(workPayrollSummaries) { summary ->
                            WorkPayrollCard(
                                strings = strings,
                                summary = summary,
                                onClick = { onWorkClick(summary.work.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WorkPayrollCard(
    strings: Strings,
    summary: WorkPayrollSummary,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Work name with icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🏗️",
                    style = MaterialTheme.typography.displaySmall
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = summary.work.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            HorizontalDivider()

            // Summary info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Employee count
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "👷", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${summary.employeeCount} ${if (summary.employeeCount != 1) strings.employees else strings.employee}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Total payment
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "💰", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "R$ ${String.format("%.2f", summary.totalPayment)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = TailwindGreen
                    )
                }
            }
        }
    }
}
