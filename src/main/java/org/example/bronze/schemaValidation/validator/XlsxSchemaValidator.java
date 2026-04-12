package org.example.bronze.schemaValidation.validator;

import org.example.bronze.schemaValidation.model.SchemaDefinition;
import org.example.bronze.schemaValidation.validator.tabular.HeaderExtractionResult;
import org.example.bronze.schemaValidation.validator.tabular.HeaderMatcher;
import org.example.bronze.schemaValidation.validator.tabular.TabularHeaderSupport;
import org.apache.poi.ss.usermodel.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class XlsxSchemaValidator implements SchemaValidator
{
    private final TabularHeaderSupport headerSupport = new TabularHeaderSupport();
    private final HeaderMatcher headerMatcher = new HeaderMatcher();
    private final DataFormatter dataFormatter = new DataFormatter();

    @Override
    public ValidationResult validate(Path dataFile, SchemaDefinition schemaDefinition) throws Exception
    {
        String fileName = dataFile.getFileName().toString().toLowerCase();
        if (!fileName.endsWith(".xlsx"))
        {
            return ValidationResult.notMatched("XLSX validation only applies to XLSX files");
        }

        try (Workbook schemaWorkbook = openWorkbook(schemaDefinition.location());
             Workbook dataWorkbook = openWorkbook(dataFile))
        {
            ValidationResult sheetValidation = validateSheetStructure(schemaWorkbook, dataWorkbook);
            if (!sheetValidation.matched())
            {
                return sheetValidation;
            }

            for (int index = 0; index < schemaWorkbook.getNumberOfSheets(); index++)
            {
                Sheet schemaSheet = schemaWorkbook.getSheetAt(index);
                Sheet dataSheet = dataWorkbook.getSheet(schemaSheet.getSheetName());

                HeaderExtractionResult schemaHeader = findHeader(
                        schemaSheet,
                        "schema workbook %s sheet %s".formatted(
                                schemaDefinition.location().getFileName(),
                                schemaSheet.getSheetName()
                        )
                );
                HeaderExtractionResult dataHeader = findHeader(
                        dataSheet,
                        "data workbook %s sheet %s".formatted(dataFile.getFileName(), dataSheet.getSheetName())
                );

                ValidationResult headerValidation = headerMatcher.validateExactMatch(
                        schemaHeader.headers(),
                        schemaHeader.locationDescription(),
                        dataHeader.headers(),
                        dataHeader.locationDescription()
                );
                if (!headerValidation.matched())
                {
                    return headerValidation;
                }
            }
        }

        return ValidationResult.matched(
                "Workbook %s matched XLSX schema %s".formatted(dataFile.getFileName(), schemaDefinition.id())
        );
    }

    private Workbook openWorkbook(Path path) throws IOException
    {
        try (InputStream inputStream = Files.newInputStream(path))
        {
            return WorkbookFactory.create(inputStream);
        }
    }

    private ValidationResult validateSheetStructure(Workbook schemaWorkbook, Workbook dataWorkbook)
    {
        int expectedSheetCount = schemaWorkbook.getNumberOfSheets();
        int actualSheetCount = dataWorkbook.getNumberOfSheets();
        if (expectedSheetCount != actualSheetCount)
        {
            return ValidationResult.notMatched(
                    "Sheet count mismatch: expected %s sheets but found %s"
                            .formatted(expectedSheetCount, actualSheetCount)
            );
        }

        for (int index = 0; index < expectedSheetCount; index++)
        {
            String expectedName = schemaWorkbook.getSheetAt(index).getSheetName();
            String actualName = dataWorkbook.getSheetAt(index).getSheetName();
            if (!expectedName.equals(actualName))
            {
                return ValidationResult.notMatched(
                        "Sheet %s mismatch: expected '%s' but found '%s'"
                                .formatted(index + 1, expectedName, actualName)
                );
            }
        }

        return ValidationResult.matched("Workbook sheet structure matched");
    }

    private HeaderExtractionResult findHeader(Sheet sheet, String locationPrefix)
    {
        int firstRow = sheet.getFirstRowNum();
        int lastRow = sheet.getLastRowNum();

        for (int rowIndex = firstRow; rowIndex <= lastRow; rowIndex++)
        {
            Row row = sheet.getRow(rowIndex);
            if (row == null)
            {
                continue;
            }

            HeaderExtractionResult header = headerSupport.extractHeader(
                    readRow(row),
                    "%s row %s".formatted(locationPrefix, rowIndex + 1)
            );
            if (header != null)
            {
                return header;
            }
        }

        throw new IllegalArgumentException("No header row was found in %s".formatted(locationPrefix));
    }

    private List<String> readRow(Row row)
    {
        List<String> cells = new ArrayList<>();
        short lastCellNum = row.getLastCellNum();
        if (lastCellNum < 0)
        {
            return cells;
        }

        for (int cellIndex = 0; cellIndex < lastCellNum; cellIndex++)
        {
            Cell cell = row.getCell(cellIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            cells.add(cell == null ? "" : dataFormatter.formatCellValue(cell));
        }
        return cells;
    }
}
