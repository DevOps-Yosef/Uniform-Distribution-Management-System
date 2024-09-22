interface Employee {
  id: string;
  badgeNumber: string;
  name: string;
  gender: string;
  uniformRequisitionCollection: UniformRequisition[];
  positionCategory: PositionCategory;
  positionCategoryName: string;
  jobPosition: string;
  jobGrade: string;
  jobCategory: string;
  department: string;
  inactive: boolean;
  status: string;
  eMail: string;
  createdBy: string;
  createdDate: Date;
  page: string;
  rowsPerPage: string;
}


interface Position {
  hrisLuPosId: string;
  name: string;
  grade: string;
  department: string;
  category: string;
  division: string;
  section: string;
  page: string;
  rowsPerPage: string;
  entitlementCollection: Entitlement[];
}

interface Entitlement {
  id: string;
  luStatusDTO: LuStatus;
  positionCategory: PositionCategory;
  positionCategoryName: string;
  entitlementUniformItemsCollection: EntitlementUniformItems[];
  createdBy: string;
  createdDate: Date;
  approvedBy: string;
  approvedDate: Date;
  email: boolean;
  remark: string;
  page: string;
  rowsPerPage: string;
  //#############################
  imsItemId: number;
  itemName: string;
  quantity: number;
  period: number;
}

interface EntitlementUniformItems {
  id: string;
  quantity: string;
  period: string;
  imsItemId: string;
  itemName: string;
  entitlement: Entitlement;
  //##########################
}

interface ImsItem {
  id: string;
  name: string;
  selected: boolean;
}

interface IdNamePair {
  id: string;
  name: string;
}

interface NameObject {
  name: string;
}

interface LuStatus {
  id: string;
  name: string;
  description: string;
}

interface PositionCategory {
  id: string;
  name: string;
  description: string;
}

interface AccessLog {
  id: string;
  date: Date;
  username: string;
  operation: string;
  objectInfo: string;
}

interface User {
  name: string;
  role: string;
  active: boolean;
  token: string;
  refreshToken: string;
}

interface EmployeeUniformItem {
  idUniformIssue: string;
  itemName: string;
  size: string;
  date: Date;
  nextDate: Date;
}

interface ForbiddenResponse {
  status: number;
  message: string;
}

//###########################################################
interface UniformRequisitionDetail {
  id: string;
  imsItemId: string;
  itemName: string;
  size: string;
  lastIssueDate: Date;
  nextIssueDate: Date;
  uniformIssueDetailCollection: UniformIssueDetail[];
  uniformRequisitionId: UniformRequisition;
  //###
  approvedBy: string;
  approvedDate: Date;
}

interface UniformIssueDetail {
  id: string;
  imsGrnDetailId: string;
  size: string;
  uniformIssue: UniformIssue;
  uniformRequisitionDetail: UniformRequisitionDetail;
}

interface UniformIssue {
  id: string;
  department: string;
  createdBy: string;
  createdDate: Date;
  approvedBy: string;
  approvedDate: Date;
  uniformIssueDetailCollection: UniformIssueDetail[];
  luStatusId: LuStatus;
  remark: string;
  //#
  name: string;
  positionCategoryName: string;
}

interface UniformRequisition {
  id: string;
  department: string;
  createdBy: string;
  createdDate: Date;
  approvedBy: string;
  approvedDate: Date;
  uniformRequisitionDetailCollection: UniformRequisitionDetail[];
  employeeId: Employee;
  luStatusId: LuStatus;
  remark: string;
}