package com.captainslog.pdf

import android.content.Context
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.interactive.form.PDAcroForm
import com.tom_roush.pdfbox.pdmodel.interactive.form.PDField
import com.tom_roush.pdfbox.pdmodel.interactive.form.PDTextField
import java.io.File
import java.io.InputStream

/**
 * Fills the CG-719S PDF form with aggregated trip/boat data.
 * Uses PdfBox-Android to fill AcroForm fields in the official USCG PDF.
 */
object CG719SPdfFiller {

    private var initialized = false

    fun init(context: Context) {
        if (!initialized) {
            PDFBoxResourceLoader.init(context.applicationContext)
            initialized = true
        }
    }

    /**
     * Fill a CG-719S form and save to the output file.
     * @param context Android context for asset access
     * @param formData The aggregated form data
     * @param outputFile Where to save the filled PDF
     */
    fun fillForm(context: Context, formData: CG719SFormData, outputFile: File): File {
        init(context)

        val inputStream: InputStream = context.assets.open("CG_719S.pdf")
        val document = PDDocument.load(inputStream)

        try {
            val acroForm = document.documentCatalog.acroForm

            if (acroForm != null) {
                // Remove XFA to force AcroForm mode (XFA can cause issues)
                acroForm.cosObject.removeItem(com.tom_roush.pdfbox.cos.COSName.getPDFName("XFA"))

                fillSectionI(acroForm, formData)
                fillSectionII(acroForm, formData)
                fillSummaryFields(acroForm, formData)
                fillSectionIII(acroForm, formData)
            }

            document.save(outputFile)
        } finally {
            document.close()
            inputStream.close()
        }

        return outputFile
    }

    private fun fillSectionI(acroForm: PDAcroForm, data: CG719SFormData) {
        setField(acroForm, "LastName", data.lastName)
        setField(acroForm, "FirstName", data.firstName)
        setField(acroForm, "MiddleName", data.middleName)
        setField(acroForm, "RefNum", data.referenceNumber)
        // SSN intentionally left blank
        setField(acroForm, "VesselName", data.vesselName)
        setField(acroForm, "OfficialNumber", data.officialNumber)
        setField(acroForm, "GrossTons", data.grossTons)
        setField(acroForm, "LengthFeet", data.lengthFeet)
        setField(acroForm, "LengthInches", data.lengthInches)
        setField(acroForm, "WidthFeet", data.widthFeet)
        setField(acroForm, "WidthInches", data.widthInches)
        setField(acroForm, "DepthFeet", data.depthFeet)
        setField(acroForm, "DepthInches", data.depthInches)
        setField(acroForm, "Propulsion", data.propulsion)
        setField(acroForm, "ServedAs", data.servedAs)
        // ServedAs[1] is the bodies of water field based on form layout
        setField(acroForm, "ServedAs", data.bodiesOfWater, index = 1)
    }

    private fun fillSectionII(acroForm: PDAcroForm, data: CG719SFormData) {
        // The monthly service table has rows for each month pair:
        // Row3-Row7: Jan-Apr (rows with multiple year entries)
        // Row10-Row14: May-Aug
        // Row17-Row20: Sep-Dec
        //
        // Each row has Cell1-Cell8: Year,Days,Year,Days,Year,Days,Year,Days
        // That gives 4 year/day pairs per month (for recording multiple years)

        // Month-to-row mapping based on form layout
        val monthRowMap = mapOf(
            1 to "Row3",   // January
            2 to "Row4",   // February (shares row block)
            3 to "Row5",   // March
            4 to "Row6",   // April
            5 to "Row10",  // May
            6 to "Row11",  // June
            7 to "Row12",  // July
            8 to "Row13",  // August
            9 to "Row17",  // September
            10 to "Row18", // October
            11 to "Row19", // November
            12 to "Row20"  // December
        )

        // But looking at the actual field structure more carefully:
        // The grid is Jan/Feb/Mar/Apr across, then May/Jun/Jul/Aug, then Sep/Oct/Nov/Dec
        // Each "row" in the grid has 8 cells: pairs of (Year, Days) for each of the 4 months
        // So Row3[0].Cell1=Jan Year, Cell2=Jan Days, Cell3=Feb Year, Cell4=Feb Days, etc.
        // Multiple Row3 entries (Row3[0], Row4[0], etc.) handle multiple years per month group

        // Month groups: (Jan,Feb,Mar,Apr), (May,Jun,Jul,Aug), (Sep,Oct,Nov,Dec)
        // Rows per group allow multiple years

        fillMonthGroup(acroForm, listOf(1, 2, 3, 4), listOf("Row3", "Row4", "Row5", "Row6", "Row7"), data)
        fillMonthGroup(acroForm, listOf(5, 6, 7, 8), listOf("Row10", "Row11", "Row12", "Row13", "Row14"), data)
        fillMonthGroup(acroForm, listOf(9, 10, 11, 12), listOf("Row17", "Row18", "Row19", "Row20"), data)
    }

    private fun fillMonthGroup(
        acroForm: PDAcroForm,
        months: List<Int>,
        rows: List<String>,
        data: CG719SFormData
    ) {
        // Find the max number of years across this month group
        val maxYears = months.maxOfOrNull { month ->
            data.monthlyService[month]?.size ?: 0
        } ?: 0

        // Each row handles one year's worth of data for all 4 months in the group
        for (yearIdx in 0 until minOf(maxYears, rows.size)) {
            val rowName = rows[yearIdx]
            for ((monthOffset, month) in months.withIndex()) {
                val yearDays = data.monthlyService[month]?.getOrNull(yearIdx)
                val yearCellIdx = monthOffset * 2 + 1 // Cell1, Cell3, Cell5, Cell7
                val daysCellIdx = monthOffset * 2 + 2 // Cell2, Cell4, Cell6, Cell8

                if (yearDays != null) {
                    setTableField(acroForm, rowName, yearCellIdx, yearDays.year.toString())
                    setTableField(acroForm, rowName, daysCellIdx, yearDays.days.toString())
                }
            }
        }
    }

    private fun fillSummaryFields(acroForm: PDAcroForm, data: CG719SFormData) {
        setField(acroForm, "DaysOnVsl", data.totalDaysServed.toString())
        setField(acroForm, "DayOnGrtLks", data.daysOnGreatLakes.toString())
        setField(acroForm, "DaysOnWaterShoreward", data.daysShoreward.toString())
        setField(acroForm, "DaysSeaward", data.daysSeaward.toString())
        setField(acroForm, "AvgHoursUnderway", data.averageHoursUnderway)
        setField(acroForm, "AvgDistanceOffShore", data.averageDistanceOffshore)
    }

    private fun fillSectionIII(acroForm: PDAcroForm, data: CG719SFormData) {
        // Owner/Operator info on page 2
        setFieldByFullName(acroForm, "form1[0].#subform[2].OwnerLName[0]", data.ownerLastName)
        setFieldByFullName(acroForm, "form1[0].#subform[2].OwnerFirstName[0]", data.ownerFirstName)
        setFieldByFullName(acroForm, "form1[0].#subform[2].OwnerMiddleName[0]", data.ownerMiddleName)
        setFieldByFullName(acroForm, "form1[0].#subform[2].OwnerStreetAddr[0]", data.ownerStreetAddress)
        setFieldByFullName(acroForm, "form1[0].#subform[2].OwnerCity[0]", data.ownerCity)
        setFieldByFullName(acroForm, "form1[0].#subform[2].OwnerState[0]", data.ownerState)
        setFieldByFullName(acroForm, "form1[0].#subform[2].OwnerZip[0]", data.ownerZipCode)
        setFieldByFullName(acroForm, "form1[0].#subform[2].EmailAddrOwner[0]", data.ownerEmail)
        setFieldByFullName(acroForm, "form1[0].#subform[2].PhoneNumberOwner[0]", data.ownerPhone)
    }

    /**
     * Set a field value by short name (searches all fields for a match).
     */
    private fun setField(acroForm: PDAcroForm, shortName: String, value: String?, index: Int = 0) {
        if (value.isNullOrBlank()) return

        val targetSuffix = if (index == 0) "$shortName[0]" else "$shortName[$index]"

        val field = findFieldBySuffix(acroForm, targetSuffix)
        if (field is PDTextField) {
            try {
                field.setValue(value)
            } catch (e: Exception) {
                // Field might not support setValue; silently skip
            }
        }
    }

    /**
     * Set a table cell field value.
     */
    private fun setTableField(acroForm: PDAcroForm, rowName: String, cellIndex: Int, value: String?) {
        if (value.isNullOrBlank()) return

        val suffix = "$rowName[0].Cell$cellIndex[0]"
        val field = findFieldBySuffix(acroForm, suffix)
        if (field is PDTextField) {
            try {
                field.setValue(value)
            } catch (e: Exception) {
                // Silently skip
            }
        }
    }

    /**
     * Set a field by its full XFA path name.
     */
    private fun setFieldByFullName(acroForm: PDAcroForm, fullName: String, value: String?) {
        if (value.isNullOrBlank()) return

        val field = acroForm.getField(fullName)
        if (field is PDTextField) {
            try {
                field.setValue(value)
            } catch (e: Exception) {
                // Silently skip
            }
        }
    }

    /**
     * Find a field whose fully qualified name ends with the given suffix.
     */
    private fun findFieldBySuffix(acroForm: PDAcroForm, suffix: String): PDField? {
        return acroForm.fieldTree.firstOrNull { field ->
            field.fullyQualifiedName.endsWith(suffix)
        }
    }
}
