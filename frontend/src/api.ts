import axios from 'axios';

export const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081/api',
});

export type Server = {
  id: string;
  name: string;
  host: string;
  environment: string;
  status: 'HEALTHY' | 'WARNING' | 'CRITICAL' | 'OFFLINE';
  cpuPercent: number | null;
  memoryPercent: number | null;
  lastHeartbeat: string | null;
};

export type Incident = {
  id: string;
  serverId: string;
  serverName: string;
  severity: 'INFO' | 'WARNING' | 'CRITICAL';
  status: 'OPEN' | 'ACKNOWLEDGED' | 'RESOLVED';
  title: string;
  description: string;
  createdAt: string;
  acknowledgedAt: string | null;
};

export type Metric = {
  recordedAt: string;
  cpuPercent: number;
  memoryPercent: number;
};

export const getServers = () =>
  api.get<Server[]>('/servers').then((r) => r.data);

export const getIncidents = () =>
  api.get<Incident[]>('/incidents').then((r) => r.data);

export const getMetrics = (id: string) =>
  api.get<Metric[]>(`/servers/${id}/metrics`).then((r) => r.data);

export const addServer = (v: {
  name: string;
  host: string;
  environment: string;
}) => api.post<Server>('/servers', v).then((r) => r.data);

export const acknowledge = (id: string) =>
  api.post(`/incidents/${id}/acknowledge`);
