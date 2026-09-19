package com.smartsched.common.validation;

import com.smartsched.common.exception.BadRequestException;

public final class AcademicValidator {

    private AcademicValidator() {
    }

    public static void validateYearSemester(Integer year, Integer semester) {

        if (year == null || semester == null) {
            throw new BadRequestException("Year and Semester are required.");
        }

        if (year < 1 || year > 4) {
            throw new BadRequestException("Year must be between 1 and 4.");
        }

        if (semester < 1 || semester > 8) {
            throw new BadRequestException("Semester must be between 1 and 8.");
        }

        switch (year) {

            case 1 -> {
                if (semester != 1 && semester != 2) {
                    throw new BadRequestException(
                            "First year can only have Semester 1 or 2.");
                }
            }

            case 2 -> {
                if (semester != 3 && semester != 4) {
                    throw new BadRequestException(
                            "Second year can only have Semester 3 or 4.");
                }
            }

            case 3 -> {
                if (semester != 5 && semester != 6) {
                    throw new BadRequestException(
                            "Third year can only have Semester 5 or 6.");
                }
            }

            case 4 -> {
                if (semester != 7 && semester != 8) {
                    throw new BadRequestException(
                            "Fourth year can only have Semester 7 or 8.");
                }
            }

            default ->
                    throw new BadRequestException("Invalid academic year.");
        }
    }
}