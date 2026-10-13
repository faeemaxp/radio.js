package com.robco.pipboyradio.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.robco.pipboyradio.data.model.Station
import com.robco.pipboyradio.ui.theme.AccentAmber
import com.robco.pipboyradio.ui.theme.AccentRed
import com.robco.pipboyradio.ui.theme.PhosphorDim
import com.robco.pipboyradio.ui.theme.PhosphorPrimary
import com.robco.pipboyradio.ui.theme.PhosphorSecondary
import com.robco.pipboyradio.ui.theme.PipBoyTypography
import com.robco.pipboyradio.ui.theme.ScreenBackground
import com.robco.pipboyradio.ui.theme.SurfaceBorder
import com.robco.pipboyradio.ui.theme.SurfaceCard

@Composable
fun StationListSection(
    stations: List<Station>,
    currentStationIndex: Int,
    searchQuery: String,
    selectedCategory: String,
    isOverdrive: Boolean,
    onSearchChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onStationSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = listOf("ALL", "WASTELAND", "VIVIDH BHARATI", "FM", "AKASHVANI")

    Box(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, if (isOverdrive) AccentRed else SurfaceBorder, RoundedCornerShape(8.dp))
            .background(SurfaceCard, RoundedCornerShape(8.dp))
            .padding(10.dp)
            .testTag("station_list_section")
    ) {
        Text(
            text = "[ COMM-LINK ]",
            style = PipBoyTypography.labelSmall.copy(
                fontSize = 11.sp,
                color = if (isOverdrive) AccentRed else PhosphorPrimary
            ),
            modifier = Modifier
                .padding(start = 8.dp)
                .background(ScreenBackground)
                .padding(horizontal = 4.dp)
                .align(Alignment.TopStart)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                label = { Text("FREQUENCY SEARCH", style = PipBoyTypography.bodyMedium) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PhosphorPrimary,
                    unfocusedBorderColor = SurfaceBorder,
                    focusedTextColor = PhosphorPrimary,
                    unfocusedTextColor = PhosphorSecondary,
                    cursorColor = AccentAmber
                ),
                textStyle = PipBoyTypography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_input")
            )

            // Category Chips Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                categories.forEach { cat ->
                    val isSelected = selectedCategory == cat
                    Box(
                        modifier = Modifier
                            .border(
                                1.dp,
                                if (isSelected) PhosphorPrimary else SurfaceBorder,
                                RoundedCornerShape(4.dp)
                            )
                            .background(
                                if (isSelected) PhosphorSecondary else Color.Transparent,
                                RoundedCornerShape(4.dp)
                            )
                            .clickable { onCategoryChange(cat) }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("category_chip_$cat")
                    ) {
                        Text(
                            text = cat,
                            style = PipBoyTypography.bodyMedium.copy(
                                fontSize = 11.sp,
                                color = if (isSelected) Color.Black else PhosphorSecondary
                            )
                        )
                    }
                }
            }

            // Signal Count
            Text(
                text = "> ${stations.size} SIGNALS AVAILABLE",
                style = PipBoyTypography.labelSmall.copy(
                    fontSize = 11.sp,
                    color = AccentAmber
                )
            )

            // Station List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .testTag("stations_lazy_column"),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (stations.isEmpty()) {
                    item {
                        Text(
                            text = "NO SIGNALS DETECTED",
                            style = PipBoyTypography.bodyMedium.copy(
                                color = PhosphorSecondary.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else {
                    itemsIndexed(stations) { index, station ->
                        val isActive = index == currentStationIndex
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = if (isActive) 1.dp else 0.dp,
                                    color = if (isActive) AccentAmber else Color.Transparent,
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .background(
                                    if (isActive) PhosphorPrimary else Color(0x22000000),
                                    RoundedCornerShape(4.dp)
                                )
                                .clickable { onStationSelect(index) }
                                .padding(horizontal = 10.dp, vertical = 8.dp)
                                .testTag("station_item_$index"),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isActive) "► ${station.name}" else station.name,
                                style = PipBoyTypography.bodyLarge.copy(
                                    color = if (isActive) Color.Black else PhosphorPrimary,
                                    fontSize = 13.sp
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )

                            Text(
                                text = station.category,
                                style = PipBoyTypography.bodyMedium.copy(
                                    fontSize = 10.sp,
                                    color = if (isActive) Color(0xFF222222) else PhosphorSecondary.copy(alpha = 0.7f)
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
