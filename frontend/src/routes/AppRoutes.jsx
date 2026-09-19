import { Routes, Route, Navigate } from "react-router-dom";
import LandingPage from "../pages/LandingPage";
import TimetableManagement from "../pages/principal/TimetableManagement";
import Login from "../pages/auth/login";
import ChangePassword from "../pages/auth/ChangePassword";
import RoomManagement from "../pages/principal/RoomManagement";
import PrincipalDashboard from "../pages/dashboard/PrincipalDashboard";

import FacultyDashboard from "../pages/dashboard/FacultyDashboard";
import FacultySubjectAssignmentManagement
from "../pages/principal/FacultySubjectAssignmentManagement";
import AdminCurriculumManagement from "../pages/admin/AdminCurriculumManagement";
import AdminFacultySubjectAssignmentManagement from "../pages/admin/AdminFacultySubjectAssignmentManagement";
import BlockManagement from "../pages/principal/BlockManagement";
import FacultyManagement from "../pages/principal/FacultyManagement";
import DepartmentAdmins from "../pages/principal/DepartmentAdmins";
import HodManagement from "../pages/principal/HodManagement";
import BranchManagement from "../pages/principal/BranchManagement";
import RegulationManagement from "../pages/principal/RegulationManagement";
import SubjectManagement from "../pages/principal/SubjectManagement";
import StudentClassManagement from "../pages/principal/StudentClassManagement";
import AcademicYearManagement from "../pages/principal/AcademicYearManagement";
import CurriculumManagement from "../pages/principal/CurriculumManagement";
import CurriculumSubjectPage from "../pages/principal/CurriculumSubjectPage";
import Reports from "../pages/principal/Reports";
import Settings from "../pages/principal/Settings";

// Admin (Department Admin) — new
import AdminDashboard from "../pages/admin/AdminDashboard";
import AdminFacultyManagement from "../pages/admin/AdminFacultyManagement";
import AdminSubjectManagement from "../pages/admin/AdminSubjectManagement";
import AdminClassManagement from "../pages/admin/AdminClassManagement";
import AdminTimetableManagement from "../pages/admin/AdminTimetableManagement";
import ProtectedRoute from "./ProtectedRoute";
import HodTimetableHistory from "../pages/hod/HodTimetableHistory";
import HodDashboard from "../pages/dashboard/HodDashboard";
import HodClassesSubjects from "../pages/hod/HodClassesSubjects";
import HodTimetableApprovals from "../pages/hod/HodTimetableApprovals";
import HodLeaveApprovals from "../pages/hod/HodLeaveApprovals";
import HodFaculty from "../pages/hod/HodFaculty";
import FacultyMySubjects from "../pages/faculty/FacultyMySubjects";
import FacultyTimetable from "../pages/faculty/FacultyTimetable";
import FacultyLeave from "../pages/faculty/FacultyLeave";

export default function AppRoutes() {

    return (

        <Routes>

            {/* Default Route */}
            <Route
                path="/"
                element={<LandingPage />}
            />
<Route
    path="/curriculum/:curriculumId/subjects"
    element={<CurriculumSubjectPage />}
/>
            <Route
                path="/login"
                element={<Login />}
            />
            <Route
    path="/admin/curriculums"
    element={
        <ProtectedRoute allowedRoles={["ADMIN"]}>
            <AdminCurriculumManagement />
        </ProtectedRoute>
    }
/>

            <Route
    path="/admin/faculty-assignments"
    element={
        <ProtectedRoute allowedRoles={["ADMIN"]}>
            <AdminFacultySubjectAssignmentManagement />
        </ProtectedRoute>
    }
/>
            <Route
    path="/principal/faculty-assignments"
    element={<FacultySubjectAssignmentManagement />}
/>

<Route
    path="/principal/blocks"
    element={<BlockManagement />}
/>
<Route
    path="/principal/subjects"
    element={<SubjectManagement />}
/>

<Route
    path="/principal/rooms"
    element={<RoomManagement />}
/>
            <Route
    path="/principal/faculties"
    element={<FacultyManagement />}
/>

            <Route
                path="/change-password"
                element={<ChangePassword />}
            />

            <Route
    path="/principal/dashboard"
    element={<PrincipalDashboard />}
/>
<Route
    path="/principal/timetable"
    element={<TimetableManagement />}
/>
<Route
    path="/principal/student-classes"
    element={<StudentClassManagement />}
/>
<Route
    path="/principal/branches"
    element={<BranchManagement />}
/>
<Route
    path="/principal/curriculums"
    element={<CurriculumManagement />}
/>
<Route
    path="/hod/dashboard"
    element={
        <ProtectedRoute allowedRoles={["HOD"]}>
            <HodDashboard />
        </ProtectedRoute>
    }
/>
<Route
    path="/hod/classes"
    element={
        <ProtectedRoute allowedRoles={["HOD"]}>
            <HodClassesSubjects />
        </ProtectedRoute>
    }
/>

<Route
    path="/hod/timetable-approvals"
    element={
        <ProtectedRoute allowedRoles={["HOD"]}>
            <HodTimetableApprovals />
        </ProtectedRoute>
    }
/>
<Route
    path="/hod/leave-approvals"
    element={
        <ProtectedRoute allowedRoles={["HOD"]}>
            <HodLeaveApprovals />
        </ProtectedRoute>
    }
/>
<Route
    path="/hod/timetable-history"
    element={
        <ProtectedRoute allowedRoles={["HOD"]}>
            <HodTimetableHistory />
        </ProtectedRoute>
    }
/>
<Route
    path="/hod/faculty"
    element={
        <ProtectedRoute allowedRoles={["HOD"]}>
            <HodFaculty />
        </ProtectedRoute>
    }
/>
<Route
    path="/principal/department-admins"
    element={<DepartmentAdmins />}
/>

<Route
    path="/principal/academic-years"
    element={<AcademicYearManagement />}
/>

<Route
    path="/principal/hods"
    element={<HodManagement />}
/>

<Route
    path="/faculty/dashboard"
    element={
        <ProtectedRoute allowedRoles={["FACULTY"]}>
            <FacultyDashboard />
        </ProtectedRoute>
    }
/>

<Route
    path="/faculty/subjects"
    element={
        <ProtectedRoute allowedRoles={["FACULTY"]}>
            <FacultyMySubjects />
        </ProtectedRoute>
    }
/>

<Route
    path="/faculty/timetable"
    element={
        <ProtectedRoute allowedRoles={["FACULTY"]}>
            <FacultyTimetable />
        </ProtectedRoute>
    }
/>

<Route
    path="/faculty/leave"
    element={
        <ProtectedRoute allowedRoles={["FACULTY"]}>
            <FacultyLeave />
        </ProtectedRoute>
    }
/>

<Route
    path="/principal/regulations"
    element={<RegulationManagement />}
/>

<Route
    path="/principal/reports"
    element={<Reports />}
/>

<Route
    path="/principal/settings"
    element={<Settings />}
/>

            {/* ================= ADMIN (Department Admin) ================= */}

            <Route
                path="/admin/dashboard"
                element={
                    <ProtectedRoute allowedRoles={["ADMIN"]}>
                        <AdminDashboard />
                    </ProtectedRoute>
                }
            />

            <Route
                path="/admin/faculties"
                element={
                    <ProtectedRoute allowedRoles={["ADMIN"]}>
                        <AdminFacultyManagement />
                    </ProtectedRoute>
                }
            />

            <Route
                path="/admin/subjects"
                element={
                    <ProtectedRoute allowedRoles={["ADMIN"]}>
                        <AdminSubjectManagement />
                    </ProtectedRoute>
                }
            />

            <Route
                path="/admin/student-classes"
                element={
                    <ProtectedRoute allowedRoles={["ADMIN"]}>
                        <AdminClassManagement />
                    </ProtectedRoute>
                }
            />

            <Route
                path="/admin/timetable"
                element={
                    <ProtectedRoute allowedRoles={["ADMIN"]}>
                        <AdminTimetableManagement />
                    </ProtectedRoute>
                }
            />

            {/* 404 */}
            <Route
                path="*"
                element={<Navigate to="/login" replace />}
            />

        </Routes>

    );
}