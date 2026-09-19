import { useNavigate } from "react-router-dom";
import {
    Box,
    Container,
    Typography,
    Button,
    Stack,
    Chip,
    Grid,
} from "@mui/material";
import ArrowForwardIcon from "@mui/icons-material/ArrowForward";
import BoltIcon from "@mui/icons-material/Bolt";
import RuleIcon from "@mui/icons-material/Rule";
import GroupsIcon from "@mui/icons-material/Groups";
import InsightsIcon from "@mui/icons-material/Insights";

/**
 * Landing page for SmartSched.
 *
 * Signature element: the hero grid is a real (simplified) mock of the
 * timetable the scheduler produces - a 5-day x 6-period board whose
 * cells light up one-by-one on load, echoing the round-robin engine
 * placing one block per subject per pass rather than filling a class
 * top-to-bottom. It's decorative here, but the metaphor is accurate
 * to how generation actually works, not a stock animation.
 */

const DAYS = ["Mon", "Tue", "Wed", "Thu", "Fri"];
const PERIOD_COUNT = 6;
const LUNCH_AFTER = 3;

// Which (day, period) cells are "filled" in the mock, and in what
// order they light up. Left intentionally uneven / not-fully-packed
// so it reads as a real timetable rather than a solid color block.
const FILL_ORDER = [
    [0, 0], [1, 0], [2, 0], [3, 0], [4, 0],
    [0, 1], [1, 1], [3, 1], [4, 1],
    [2, 2], [0, 2], [4, 2],
    [1, 3], [3, 3], [0, 3],
    [2, 4], [4, 4], [1, 4],
    [0, 5], [3, 5],
];

const SUBJECT_COLORS = [
    "#0D47A1",
    "#F2A93B",
    "#2E7D6B",
    "#8E5CD9",
    "#D9564C",
];

function HeroGrid() {
    const cellKey = (d, p) => `${d}-${p}`;
    const fillIndex = new Map(
        FILL_ORDER.map(([d, p], i) => [cellKey(d, p), i])
    );

    return (
        <Box
            sx={{
                display: "grid",
                gridTemplateColumns: `56px repeat(${DAYS.length}, 1fr)`,
                gap: "6px",
                p: 2.5,
                borderRadius: 3,
                background: "rgba(255,255,255,0.06)",
                border: "1px solid rgba(255,255,255,0.14)",
                backdropFilter: "blur(6px)",
            }}
        >
            <Box />
            {DAYS.map((day) => (
                <Typography
                    key={day}
                    align="center"
                    sx={{
                        fontFamily: "Inter, sans-serif",
                        fontSize: 12,
                        fontWeight: 600,
                        letterSpacing: "0.06em",
                        color: "rgba(255,255,255,0.55)",
                        pb: 0.5,
                    }}
                >
                    {day.toUpperCase()}
                </Typography>
            ))}

            {Array.from({ length: PERIOD_COUNT }).map((_, p) => (
                <Box key={p} sx={{ display: "contents" }}>
                    <Typography
                        sx={{
                            fontFamily: "Inter, sans-serif",
                            fontSize: 11,
                            color: "rgba(255,255,255,0.4)",
                            display: "flex",
                            alignItems: "center",
                        }}
                    >
                        P{p + 1}
                    </Typography>

                    {DAYS.map((_, d) => {
                        const idx = fillIndex.get(cellKey(d, p));
                        const filled = idx !== undefined;
                        const isLunch = p === LUNCH_AFTER;

                        if (isLunch) {
                            return (
                                <Box
                                    key={d}
                                    sx={{
                                        height: 30,
                                        borderRadius: 1,
                                        background:
                                            "repeating-linear-gradient(135deg, rgba(255,255,255,0.05) 0 6px, rgba(255,255,255,0.02) 6px 12px)",
                                    }}
                                />
                            );
                        }

                        return (
                            <Box
                                key={d}
                                className={filled ? "sched-cell-fill" : ""}
                                sx={{
                                    height: 30,
                                    borderRadius: 1,
                                    background: filled
                                        ? SUBJECT_COLORS[idx % SUBJECT_COLORS.length]
                                        : "rgba(255,255,255,0.05)",
                                    opacity: filled ? 0 : 1,
                                    animationDelay: filled
                                        ? `${0.4 + idx * 0.09}s`
                                        : undefined,
                                }}
                            />
                        );
                    })}
                </Box>
            ))}
        </Box>
    );
}

const FEATURES = [
    {
        icon: <RuleIcon />,
        title: "Conflicts are structurally impossible",
        body:
            "Every faculty, room, and class slot is checked before a period is ever placed - not validated afterward. A double-booking can't reach the timetable in the first place.",
    },
    {
        icon: <BoltIcon />,
        title: "Generates in seconds, explains its gaps",
        body:
            "A round-robin allocator shares faculty and room capacity fairly across every subject. If something genuinely can't fit, you get the exact reason - not a silent failure.",
    },
    {
        icon: <GroupsIcon />,
        title: "Built for how departments actually work",
        body:
            "Principals, HODs, faculty, and admins each see exactly what their role needs - from approvals to workload to their own weekly schedule.",
    },
    {
        icon: <InsightsIcon />,
        title: "Balances load, not just slots",
        body:
            "Weekly and daily caps, lunch breaks, back-to-back limits, and lab-block continuity are all honoured automatically, so timetables come out usable, not just technically valid.",
    },
];

const ROLES = ["Principal", "HOD", "Faculty", "Department Admin"];

export default function LandingPage() {
    const navigate = useNavigate();

    return (
        <Box sx={{ background: "#F7F5EF", overflowX: "hidden" }}>
            <style>{`
                @keyframes schedCellFill {
                    from { opacity: 0; transform: scale(0.6); }
                    to   { opacity: 1; transform: scale(1); }
                }
                .sched-cell-fill {
                    animation: schedCellFill 0.35s ease-out forwards;
                }
                @media (prefers-reduced-motion: reduce) {
                    .sched-cell-fill {
                        animation: none !important;
                        opacity: 1 !important;
                    }
                }
            `}</style>

            {/* NAV */}
            <Container maxWidth="lg">
                <Stack
                    direction="row"
                    alignItems="center"
                    justifyContent="space-between"
                    sx={{ pt: 3, pb: 2 }}
                >
                    <Stack direction="row" alignItems="center" spacing={1.2}>
                        <Box
                            sx={{
                                width: 34,
                                height: 34,
                                borderRadius: "9px",
                                background: "#0D47A1",
                                display: "flex",
                                alignItems: "center",
                                justifyContent: "center",
                                fontFamily: "Poppins, sans-serif",
                                fontWeight: 700,
                                color: "#fff",
                                fontSize: 16,
                            }}
                        >
                            S
                        </Box>
                        <Typography
                            sx={{
                                fontFamily: "Poppins, sans-serif",
                                fontWeight: 700,
                                fontSize: 20,
                                color: "#0B1B3A",
                            }}
                        >
                            SmartSched
                        </Typography>
                    </Stack>

                    <Button
                        variant="contained"
                        disableElevation
                        onClick={() => navigate("/login")}
                        sx={{
                            borderRadius: "999px",
                            px: 3,
                            py: 1,
                            fontFamily: "Inter, sans-serif",
                            fontWeight: 600,
                            textTransform: "none",
                            background: "#0D47A1",
                            "&:hover": { background: "#0B3A85" },
                        }}
                    >
                        Log in
                    </Button>
                </Stack>
            </Container>

            {/* HERO */}
            <Box
                sx={{
                    background:
                        "linear-gradient(180deg, #0B1B3A 0%, #0D2A5C 100%)",
                    borderRadius: { xs: 0, md: "0 0 32px 32px" },
                    mt: { xs: 0, md: 1 },
                }}
            >
                <Container maxWidth="lg">
                    <Grid
                        container
                        spacing={6}
                        alignItems="center"
                        sx={{ py: { xs: 8, md: 12 } }}
                    >
                        <Grid size={{ xs: 12, md: 6 }}>
                            <Chip
                                label="Academic timetabling, automated"
                                sx={{
                                    mb: 3,
                                    background: "rgba(242,169,59,0.15)",
                                    color: "#F2A93B",
                                    fontFamily: "Inter, sans-serif",
                                    fontWeight: 600,
                                    fontSize: 12,
                                    letterSpacing: "0.03em",
                                }}
                            />

                            <Typography
                                sx={{
                                    fontFamily: "Poppins, sans-serif",
                                    fontWeight: 700,
                                    fontSize: { xs: 34, md: 46 },
                                    lineHeight: 1.12,
                                    color: "#FFFFFF",
                                    letterSpacing: "-0.01em",
                                }}
                            >
                                Every class,
                                <br />
                                every faculty,
                                <br />
                                <Box component="span" sx={{ color: "#F2A93B" }}>
                                    one clean timetable.
                                </Box>
                            </Typography>

                            <Typography
                                sx={{
                                    fontFamily: "Inter, sans-serif",
                                    fontSize: 16,
                                    lineHeight: 1.7,
                                    color: "rgba(255,255,255,0.72)",
                                    mt: 3,
                                    maxWidth: 460,
                                }}
                            >
                                SmartSched generates conflict-free weekly
                                timetables for every class in a department -
                                balancing faculty workload, room type, lunch
                                breaks, and lab continuity automatically, then
                                routes each one through HOD approval before it
                                goes live.
                            </Typography>

                            <Stack
                                direction={{ xs: "column", sm: "row" }}
                                spacing={2}
                                sx={{ mt: 5 }}
                            >
                                <Button
                                    variant="contained"
                                    disableElevation
                                    size="large"
                                    endIcon={<ArrowForwardIcon />}
                                    onClick={() => navigate("/login")}
                                    sx={{
                                        borderRadius: "999px",
                                        px: 3.5,
                                        py: 1.4,
                                        fontFamily: "Inter, sans-serif",
                                        fontWeight: 600,
                                        textTransform: "none",
                                        fontSize: 15,
                                        background: "#F2A93B",
                                        color: "#1A1200",
                                        "&:hover": { background: "#E09A2C" },
                                    }}
                                >
                                    Log in to your dashboard
                                </Button>
                            </Stack>
                        </Grid>

                        <Grid size={{ xs: 12, md: 6 }}>
                            <HeroGrid />
                        </Grid>
                    </Grid>
                </Container>
            </Box>

            {/* WHAT IT IS */}
            <Container maxWidth="md" sx={{ py: { xs: 8, md: 10 } }}>
                <Typography
                    sx={{
                        fontFamily: "Poppins, sans-serif",
                        fontWeight: 700,
                        fontSize: { xs: 24, md: 30 },
                        color: "#0B1B3A",
                        mb: 2,
                    }}
                >
                    What SmartSched actually does
                </Typography>

                <Typography
                    sx={{
                        fontFamily: "Inter, sans-serif",
                        fontSize: 16,
                        lineHeight: 1.8,
                        color: "#3A4658",
                    }}
                >
                    Manually building a department's weekly timetable means
                    juggling dozens of subjects, faculty availability, room
                    types, and workload limits at once - and it usually breaks
                    the moment one thing changes. SmartSched replaces that
                    spreadsheet with a scheduling engine: it places every
                    subject's required weekly hours for every class, checking
                    faculty, room, and class availability before each period is
                    assigned, so a clash simply can't happen. What it can't
                    place, it reports clearly - which subject, how many hours,
                    and why - instead of failing silently.
                </Typography>
            </Container>

            {/* FEATURES */}
            <Box sx={{ background: "#F0EDE3", py: { xs: 8, md: 10 } }}>
                <Container maxWidth="lg">
                    <Grid container spacing={3}>
                        {FEATURES.map((feature) => (
                            <Grid size={{ xs: 12, sm: 6 }} key={feature.title}>
                                <Box
                                    sx={{
                                        height: "100%",
                                        p: 3.5,
                                        borderRadius: 3,
                                        background: "#FFFFFF",
                                        border: "1px solid #E4DFD0",
                                    }}
                                >
                                    <Box
                                        sx={{
                                            width: 40,
                                            height: 40,
                                            borderRadius: "10px",
                                            background: "rgba(13,71,161,0.08)",
                                            color: "#0D47A1",
                                            display: "flex",
                                            alignItems: "center",
                                            justifyContent: "center",
                                            mb: 2,
                                        }}
                                    >
                                        {feature.icon}
                                    </Box>

                                    <Typography
                                        sx={{
                                            fontFamily: "Poppins, sans-serif",
                                            fontWeight: 600,
                                            fontSize: 17,
                                            color: "#0B1B3A",
                                            mb: 1,
                                        }}
                                    >
                                        {feature.title}
                                    </Typography>

                                    <Typography
                                        sx={{
                                            fontFamily: "Inter, sans-serif",
                                            fontSize: 14.5,
                                            lineHeight: 1.7,
                                            color: "#5A6478",
                                        }}
                                    >
                                        {feature.body}
                                    </Typography>
                                </Box>
                            </Grid>
                        ))}
                    </Grid>
                </Container>
            </Box>

            {/* ROLES */}
            <Container maxWidth="md" sx={{ py: { xs: 8, md: 10 }, textAlign: "center" }}>
                <Typography
                    sx={{
                        fontFamily: "Poppins, sans-serif",
                        fontWeight: 700,
                        fontSize: { xs: 22, md: 26 },
                        color: "#0B1B3A",
                        mb: 3,
                    }}
                >
                    Built for the whole department, not just the scheduler
                </Typography>

                <Stack
                    direction="row"
                    spacing={1.5}
                    justifyContent="center"
                    flexWrap="wrap"
                    useFlexGap
                >
                    {ROLES.map((role) => (
                        <Chip
                            key={role}
                            label={role}
                            sx={{
                                fontFamily: "Inter, sans-serif",
                                fontWeight: 600,
                                fontSize: 13.5,
                                px: 1.5,
                                py: 2.4,
                                background: "#FFFFFF",
                                border: "1px solid #E4DFD0",
                                color: "#0D47A1",
                            }}
                        />
                    ))}
                </Stack>
            </Container>

            {/* CTA */}
            <Container maxWidth="lg" sx={{ pb: { xs: 8, md: 10 } }}>
                <Box
                    sx={{
                        borderRadius: 4,
                        background: "#0D47A1",
                        px: { xs: 4, md: 8 },
                        py: { xs: 5, md: 6 },
                        display: "flex",
                        flexDirection: { xs: "column", md: "row" },
                        alignItems: { xs: "flex-start", md: "center" },
                        justifyContent: "space-between",
                        gap: 3,
                    }}
                >
                    <Box>
                        <Typography
                            sx={{
                                fontFamily: "Poppins, sans-serif",
                                fontWeight: 700,
                                fontSize: { xs: 22, md: 26 },
                                color: "#FFFFFF",
                            }}
                        >
                            Your timetable is one login away.
                        </Typography>
                        <Typography
                            sx={{
                                fontFamily: "Inter, sans-serif",
                                fontSize: 14.5,
                                color: "rgba(255,255,255,0.75)",
                                mt: 0.5,
                            }}
                        >
                            Sign in with your department credentials to get
                            started.
                        </Typography>
                    </Box>

                    <Button
                        variant="contained"
                        disableElevation
                        size="large"
                        endIcon={<ArrowForwardIcon />}
                        onClick={() => navigate("/login")}
                        sx={{
                            borderRadius: "999px",
                            px: 3.5,
                            py: 1.3,
                            fontFamily: "Inter, sans-serif",
                            fontWeight: 600,
                            textTransform: "none",
                            fontSize: 15,
                            background: "#F2A93B",
                            color: "#1A1200",
                            whiteSpace: "nowrap",
                            "&:hover": { background: "#E09A2C" },
                        }}
                    >
                        Log in
                    </Button>
                </Box>
            </Container>

            {/* FOOTER */}
            <Container maxWidth="lg" sx={{ pb: 5 }}>
                <Typography
                    align="center"
                    sx={{
                        fontFamily: "Inter, sans-serif",
                        fontSize: 12.5,
                        color: "#9A9584",
                    }}
                >
                    SmartSched - Automated Academic Timetabling
                </Typography>
            </Container>
        </Box>
    );
}