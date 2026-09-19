import { useEffect, useState } from "react";

import {
    Box,
    Typography,
    Paper,
    Alert,
    CircularProgress,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Chip
} from "@mui/material";

import { getMyTimetable } from "../../services/Facultyselfservice";

const DAYS = [
    "MONDAY",
    "TUESDAY",
    "WEDNESDAY",
    "THURSDAY",
    "FRIDAY",
    "SATURDAY"
];

const DAY_LABELS = {
    MONDAY: "Monday",
    TUESDAY: "Tuesday",
    WEDNESDAY: "Wednesday",
    THURSDAY: "Thursday",
    FRIDAY: "Friday",
    SATURDAY: "Saturday"
};

const FacultyTimetable = () => {

    const [entries, setEntries] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {

        loadTimetable();

    }, []);

    const loadTimetable = async () => {

        try {

            setLoading(true);
            setError("");

            const data = await getMyTimetable();

            setEntries(Array.isArray(data) ? data : []);

        } catch (err) {

            console.error("Unable to load timetable:", err);

            setError(
                err.response?.data?.message ||
                err.message ||
                "Unable to load your timetable."
            );

        } finally {

            setLoading(false);

        }
    };

    const periods = Array.from(
        new Set(entries.map((e) => e.period))
    ).sort((a, b) => a - b);

    const periodList = periods.length > 0
        ? periods
        : [1, 2, 3, 4, 5, 6, 7];

    const findEntry = (day, period) =>
        entries.find((e) => e.day === day && e.period === period);

    if (loading) {

        return (

            <Box display="flex" justifyContent="center" mt={6}>
                <CircularProgress />
            </Box>

        );
    }

    return (

        <Box>

            <Typography variant="h4" fontWeight="bold" mb={1}>
                My Timetable
            </Typography>

            <Typography variant="body2" color="text.secondary" mb={3}>
                Your weekly teaching schedule
            </Typography>

            {error && (

                <Alert severity="error" sx={{ mb: 2 }}>
                    {error}
                </Alert>

            )}

            {entries.length === 0 && !error ? (

                <Paper sx={{ p: 4, textAlign: "center" }}>

                    <Typography color="text.secondary">
                        Your timetable hasn't been published yet.
                    </Typography>

                </Paper>

            ) : (

                <Paper sx={{ p: 2, overflowX: "auto" }}>

                    <TableContainer>

                        <Table size="small" sx={{ minWidth: 700 }}>

                            <TableHead>

                                <TableRow>

                                    <TableCell sx={{ fontWeight: "bold" }}>
                                        Period
                                    </TableCell>

                                    {DAYS.map((day) => (
                                        <TableCell
                                            key={day}
                                            align="center"
                                            sx={{ fontWeight: "bold" }}
                                        >
                                            {DAY_LABELS[day]}
                                        </TableCell>
                                    ))}

                                </TableRow>

                            </TableHead>

                            <TableBody>

                                {periodList.map((period) => (

                                    <TableRow key={period}>

                                        <TableCell sx={{ fontWeight: "bold" }}>
                                            {period}
                                        </TableCell>

                                        {DAYS.map((day) => {

                                            const entry = findEntry(day, period);

                                            return (

                                                <TableCell key={day} align="center">

                                                    {entry ? (

                                                        <Box>

                                                            <Typography
                                                                variant="body2"
                                                                fontWeight="bold"
                                                            >
                                                                {entry.subject}
                                                            </Typography>

                                                            <Typography
                                                                variant="caption"
                                                                color="text.secondary"
                                                                display="block"
                                                            >
                                                                {entry.branch} Y{entry.year}
                                                                {entry.section
                                                                    ? `-${entry.section}`
                                                                    : ""}
                                                                {" "}
                                                                Sem {entry.semester}
                                                            </Typography>

                                                            {entry.room && (
                                                                <Chip
                                                                    label={entry.room}
                                                                    size="small"
                                                                    sx={{ mt: 0.5 }}
                                                                />
                                                            )}

                                                        </Box>

                                                    ) : (

                                                        <Typography
                                                            variant="caption"
                                                            color="text.disabled"
                                                        >
                                                            —
                                                        </Typography>

                                                    )}

                                                </TableCell>

                                            );
                                        })}

                                    </TableRow>

                                ))}

                            </TableBody>

                        </Table>

                    </TableContainer>

                </Paper>

            )}

        </Box>

    );

};

export default FacultyTimetable;
