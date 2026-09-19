import {
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Paper,
    Typography,
    Box,
    Chip
} from "@mui/material";

const DAYS = [
    "MONDAY",
    "TUESDAY",
    "WEDNESDAY",
    "THURSDAY",
    "FRIDAY",
    "SATURDAY"
];

 const PERIODS = [
    {
        number: 1,
        time: "09:00 - 09:50"
    },
    {
        number: 2,
        time: "09:50 - 10:40"
    },
    {
        number: 3,
        time: "11:00 - 11:50"
    },
    {
        number: 4,
        time: "11:50 - 12:40"
    },
    {
        number: 5,
        time: "01:30 - 02:20"
    },
    {
        number: 6,
        time: "02:20 - 03:10"
    },
    {
        number: 7,
        time: "03:20 - 04:10"
    }
];
/**
 * Determines if a timetable entry is for a lab subject.
 * 
 * Uses multiple fallback methods to determine if it's a lab:
 * 1. Check requiresLabRoom field (most reliable - from subject data)
 * 2. Check subjectType field (THEORY, LAB, THEORY_LAB)
 * 3. Check if room name contains "lab" (fallback)
 * 4. Check if subject name contains "lab" (least reliable)
 * 
 * @param {Object} entry - The timetable entry object
 * @returns {boolean} true if entry is for a lab subject
 */
const isLabEntry = (entry) => {
    if (!entry) return false;

    // Method 1: Direct field - most reliable
    if (entry.requiresLabRoom !== undefined) {
        return entry.requiresLabRoom === true;
    }

    // Method 2: Subject type field
    if (entry.subjectType) {
        const type = String(entry.subjectType).toUpperCase();
        return type === "LAB" || type === "THEORY_LAB";
    }

    // Method 3: Check room name as fallback
    if (entry.room) {
        const roomName = String(entry.room).toLowerCase();
        if (roomName.includes("lab")) {
            return true;
        }
    }

    // Method 4: Check subject name as last resort (least reliable)
    if (entry.subject) {
        const subjectName = String(entry.subject).toLowerCase();
        if (subjectName.includes("lab")) {
            return true;
        }
    }

    return false;
};

const TimetableGrid = ({ timetable = [] , title = "Timetable"}) => {

    /*
     * Convert the flat backend response into:
     *
     * timetableMap["MONDAY"][1]
     * timetableMap["TUESDAY"][4]
     * etc.
     */
    const timetableMap = {};

    DAYS.forEach((day) => {
        timetableMap[day] = {};
    });

    timetable.forEach((entry) => {

        if (!entry?.day || entry?.periodNumber == null) {
            return;
        }

        const day = String(entry.day).toUpperCase();
        const period = Number(entry.periodNumber);

        if (!timetableMap[day]) {
            timetableMap[day] = {};
        }

        timetableMap[day][period] = entry;
    });

    if (!timetable || timetable.length === 0) {
        return (
            <Typography
                variant="body1"
                sx={{
                    mt: 3,
                    p: 3,
                    textAlign: "center",
                    color: "text.secondary"
                }}
            >
                No timetable available.
            </Typography>
        );
    }

    return (
        <Box sx={{ mt: 4 }}>

            {title && (

                <Typography
                    variant="h5"
                    sx={{
                        mb: 2,
                        fontWeight: 600
                    }}
                >
                    {title}
                </Typography>

            )}
            <TableContainer
                component={Paper}
                elevation={2}
                sx={{
                    overflowX: "auto",
                    borderRadius: 2
                }}
            >

                <Table
                    stickyHeader
                    sx={{
                        minWidth: 1400,
                        tableLayout: "fixed"
                    }}
                >

                    {/* ================= HEADER ================= */}

                    <TableHead>

                        <TableRow>

                            <TableCell
                                sx={{
                                    width: 150,
                                    minWidth: 150,
                                    fontWeight: 700,
                                    backgroundColor: "#f5f5f5",
                                    textAlign: "center"
                                }}
                            >
                                Day / Period
                            </TableCell>

                            {PERIODS.map((period) => (

                                <TableCell
                                    key={period.number}
                                    align="center"
                                    sx={{
                                        minWidth: 155,
                                        fontWeight: 700,
                                        backgroundColor: "#f5f5f5",
                                        borderLeft: "1px solid #ddd"
                                    }}
                                >

                                    <Typography
                                        variant="subtitle1"
                                        fontWeight={700}
                                    >
                                        {period.number}
                                    </Typography>

                                    <Typography
                                        variant="caption"
                                        color="text.secondary"
                                    >
                                        {period.time}
                                    </Typography>

                                </TableCell>

                            ))}

                        </TableRow>

                    </TableHead>

                    {/* ================= BODY ================= */}

                    <TableBody>

                        {DAYS.map((day) => (

                            <TableRow key={day}>

                                {/* DAY */}

                                <TableCell
                                    sx={{
                                        fontWeight: 700,
                                        textAlign: "center",
                                        backgroundColor: "#fafafa",
                                        borderRight: "1px solid #ddd"
                                    }}
                                >
                                    {day}
                                </TableCell>

                                {/* PERIODS */}

                                {PERIODS.map((period) => {

                                    const entry =
                                        timetableMap[day]?.[period.number];

                                    // FIX: Use proper isLabEntry function instead of string matching
                                    const isLab = isLabEntry(entry);

                                    return (

                                        <TableCell
                                            key={`${day}-${period.number}`}
                                            align="center"
                                            sx={{
                                                height: 110,
                                                padding: 1,
                                                borderLeft:
                                                    "1px solid #eeeeee",
                                                verticalAlign: "middle"
                                            }}
                                        >

                                            {!entry ? (

                                                <Typography
                                                    color="text.disabled"
                                                    variant="body2"
                                                >
                                                    -
                                                </Typography>

                                            ) : (

                                                <Box
                                                    sx={{
                                                        minHeight: 85,
                                                        display: "flex",
                                                        flexDirection:
                                                            "column",
                                                        justifyContent:
                                                            "center",
                                                        alignItems:
                                                            "center",
                                                        borderRadius: 2,
                                                        padding: 1,
                                                        // FIX: Use isLab variable instead of string matching
                                                        backgroundColor: isLab
                                                            ? "#e3f2fd"
                                                            : "#fff8e1",
                                                        border: isLab
                                                            ? "1px solid #90caf9"
                                                            : "1px solid #ffcc80"
                                                    }}
                                                >

                                                    {/* SUBJECT */}

                                                    <Typography
                                                        variant="body2"
                                                        fontWeight={700}
                                                        sx={{
                                                            lineHeight: 1.2,
                                                            mb: 0.5
                                                        }}
                                                    >
                                                        {entry.subject || "-"}
                                                    </Typography>

                                                    {/* FACULTY */}

                                                    <Typography
                                                        variant="caption"
                                                        sx={{
                                                            lineHeight: 1.2
                                                        }}
                                                    >
                                                        {entry.faculty || "-"}
                                                    </Typography>

                                                    {/* ROOM */}

                                                    <Typography
                                                        variant="caption"
                                                        sx={{
                                                            lineHeight: 1.2
                                                        }}
                                                    >
                                                        {entry.room || "-"}
                                                    </Typography>

                                                    {/* STATUS */}

                                                    <Chip
                                                        label={
                                                            entry.approved
                                                                ? "APPROVED"
                                                                : "PENDING"
                                                        }
                                                        size="small"
                                                        color={
                                                            entry.approved
                                                                ? "success"
                                                                : "warning"
                                                        }
                                                        sx={{
                                                            mt: 0.7,
                                                            height: 20,
                                                            fontSize: "0.65rem"
                                                        }}
                                                    />

                                                </Box>

                                            )}

                                        </TableCell>

                                    );

                                })}

                            </TableRow>

                        ))}

                    </TableBody>

                </Table>

            </TableContainer>

            {/* ================= LEGEND ================= */}

            <Box
                sx={{
                    display: "flex",
                    justifyContent: "center",
                    gap: 4,
                    mt: 2,
                    flexWrap: "wrap"
                }}
            >

                <Box
                    sx={{
                        display: "flex",
                        alignItems: "center",
                        gap: 1
                    }}
                >

                    <Box
                        sx={{
                            width: 18,
                            height: 18,
                            borderRadius: 1,
                            backgroundColor: "#fff8e1",
                            border: "1px solid #ffcc80"
                        }}
                    />

                    <Typography variant="body2">
                        Theory
                    </Typography>

                </Box>

                <Box
                    sx={{
                        display: "flex",
                        alignItems: "center",
                        gap: 1
                    }}
                >

                    <Box
                        sx={{
                            width: 18,
                            height: 18,
                            borderRadius: 1,
                            backgroundColor: "#e3f2fd",
                            border: "1px solid #90caf9"
                        }}
                    />

                    <Typography variant="body2">
                        Practical / Lab
                    </Typography>

                </Box>

                <Box
                    sx={{
                        display: "flex",
                        alignItems: "center",
                        gap: 1
                    }}
                >

                    <Box
                        sx={{
                            width: 18,
                            height: 18,
                            borderRadius: 1,
                            backgroundColor: "#f5f5f5",
                            border: "1px solid #ddd"
                        }}
                    />

                    <Typography variant="body2">
                        Free
                    </Typography>

                </Box>

            </Box>

        </Box>
    );
};

export default TimetableGrid;