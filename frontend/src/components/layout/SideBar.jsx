import {
    Drawer,
    Toolbar,
    List,
    ListItemButton,
    ListItemIcon,
    ListItemText,
    Typography
} from "@mui/material";
import BusinessIcon from "@mui/icons-material/Business";
import { Link, useLocation } from "react-router-dom";
import MeetingRoomIcon from "@mui/icons-material/MeetingRoom";
import DashboardIcon from "@mui/icons-material/Dashboard";
import SchoolIcon from "@mui/icons-material/School";
import PeopleIcon from "@mui/icons-material/People";
import MenuBookIcon from "@mui/icons-material/MenuBook";
import EventNoteIcon from "@mui/icons-material/EventNote";
import GavelIcon from "@mui/icons-material/Gavel";
import CalendarMonthIcon from "@mui/icons-material/CalendarMonth";
import BarChartIcon from "@mui/icons-material/BarChart";
import SettingsIcon from "@mui/icons-material/Settings";
import LibraryBooksIcon from "@mui/icons-material/LibraryBooks";
import AssignmentIndIcon from "@mui/icons-material/AssignmentInd";
import HistoryIcon from "@mui/icons-material/History";
import useAuth from "../../hooks/useAuth";

const drawerWidth = 250;

const principalMenuItems = [

    { text: "Dashboard", path: "/principal/dashboard", icon: <DashboardIcon /> },
    { text: "Branches", path: "/principal/branches", icon: <SchoolIcon /> },
    { text: "Department Admins", path: "/principal/department-admins", icon: <PeopleIcon /> },
    { text: "HODs", path: "/principal/hods", icon: <PeopleIcon /> },
    { text: "Student Classes", path: "/principal/student-classes", icon: <SchoolIcon /> },
    { text: "Blocks", path: "/principal/blocks", icon: <BusinessIcon /> },
    { text: "Rooms", path: "/principal/rooms", icon: <MeetingRoomIcon /> },
    { text: "Curriculum", path: "/principal/curriculums", icon: <LibraryBooksIcon /> },
    { text: "Faculty", path: "/principal/faculties", icon: <PeopleIcon /> },
    { text: "Faculty Assignments", path: "/principal/faculty-assignments", icon: <AssignmentIndIcon /> },
    { text: "Subjects", path: "/principal/subjects", icon: <MenuBookIcon /> },
    { text: "Academic Years", path: "/principal/academic-years", icon: <EventNoteIcon /> },
    { text: "Regulations", path: "/principal/regulations", icon: <GavelIcon /> },
    { text: "Timetable", path: "/principal/timetable", icon: <CalendarMonthIcon /> },
    { text: "Reports", path: "/principal/reports", icon: <BarChartIcon /> },
    { text: "Settings", path: "/principal/settings", icon: <SettingsIcon /> }

];

// Department Admin — scoped to their own branch only
const adminMenuItems = [

    { text: "Dashboard", path: "/admin/dashboard", icon: <DashboardIcon /> },
    { text: "Faculty", path: "/admin/faculties", icon: <PeopleIcon /> },
    { text: "Faculty Assignments", path: "/admin/faculty-assignments", icon: <AssignmentIndIcon /> },
    { text: "Curriculum", path: "/admin/curriculums", icon: <LibraryBooksIcon /> },
    { text: "Subjects", path: "/admin/subjects", icon: <MenuBookIcon /> },
    { text: "Student Classes", path: "/admin/student-classes", icon: <SchoolIcon /> },
    { text: "Timetable", path: "/admin/timetable", icon: <CalendarMonthIcon /> }

];

// HOD — scoped to their own branch, read-only on classes/subjects,
// with approve/reject authority over timetables submitted by the
// department Admin.
const hodMenuItems = [

    { text: "Dashboard", path: "/hod/dashboard", icon: <DashboardIcon /> },
    { text: "Classes & Subjects", path: "/hod/classes", icon: <SchoolIcon /> },
    { text: "Timetable Approvals", path: "/hod/timetable-approvals", icon: <CalendarMonthIcon /> },
    { text: "Approval History", path: "/hod/timetable-history", icon: <HistoryIcon /> },
    { text: "Leave Requests", path: "/hod/leave-approvals", icon: <EventNoteIcon /> },
    { text: "Faculty", path: "/hod/faculty", icon: <PeopleIcon /> }

];

const facultyMenuItems = [

    { text: "Dashboard", path: "/faculty/dashboard", icon: <DashboardIcon /> },
    { text: "My Subjects", path: "/faculty/subjects", icon: <MenuBookIcon /> },
    { text: "My Timetable", path: "/faculty/timetable", icon: <CalendarMonthIcon /> },
    { text: "Leave Requests", path: "/faculty/leave", icon: <EventNoteIcon /> }

];

const menuByRole = {
    PRINCIPAL: principalMenuItems,
    ADMIN: adminMenuItems,
    HOD: hodMenuItems,
    FACULTY: facultyMenuItems
};

const Sidebar = () => {

    const location = useLocation();
    const { user } = useAuth();

    const menuItems = menuByRole[user?.role] || [];

    return (

        <Drawer
            variant="permanent"
            sx={{
                width: drawerWidth,
                flexShrink: 0,
                "& .MuiDrawer-paper": {
                    width: drawerWidth,
                    boxSizing: "border-box",
                    backgroundColor: "#0D47A1",
                    color: "white"
                }
            }}
        >

            <Toolbar>

                <Typography
                    variant="h5"
                    fontWeight="bold"
                >
                    SmartSched
                </Typography>

            </Toolbar>

            {(user?.role === "ADMIN" || user?.role === "HOD") && user?.branchName && (

                <Typography
                    variant="caption"
                    sx={{ px: 2, pb: 1, display: "block", opacity: 0.75 }}
                >
                    {user.branchName} Department
                </Typography>

            )}

            <List>

                {menuItems.map((item) => (

                    <ListItemButton
                        key={item.text}
                        component={Link}
                        to={item.path}
                        selected={location.pathname === item.path}
                        sx={{
                            "&.Mui-selected": {
                                backgroundColor: "#1565C0"
                            },
                            "&.Mui-selected:hover": {
                                backgroundColor: "#1976D2"
                            },
                            "&:hover": {
                                backgroundColor: "#1565C0"
                            }
                        }}
                    >

                        <ListItemIcon sx={{ color: "white" }}>
                            {item.icon}
                        </ListItemIcon>

                        <ListItemText primary={item.text} />

                    </ListItemButton>

                ))}

            </List>

        </Drawer>

    );

};

export default Sidebar;