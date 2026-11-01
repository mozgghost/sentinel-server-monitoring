import { api } from './api';

export type Severity = 'WARNING' | 'CRITICAL';
export type MetricType = 'CPU' | 'MEMORY';

export type AlertRule = {
  id: string;
  name: string;
  description: string;
  metric: MetricType;
  operator: '>' | '>=';
  threshold: number;
  severity: Severity;
  enabled: boolean;
  lastTriggered: string | null;
  createdAt: string;
};

export type CreateAlertRuleData = {
  name: string;
  description: string;
  metric: MetricType;
  operator: '>' | '>=';
  threshold: number;
  severity: Severity;
};

export async function getAlertRules(): Promise<AlertRule[]> {
  const response = await api.get<AlertRule[]>('/alert-rules');
  return response.data;
}

export async function createAlertRule(
  data: CreateAlertRuleData
): Promise<AlertRule> {
  const response = await api.post<AlertRule>('/alert-rules', data);
  return response.data;
}

export async function setAlertRuleEnabled(
  id: string,
  enabled: boolean
): Promise<AlertRule> {
  const response = await api.patch<AlertRule>(
    `/alert-rules/${id}/enabled`,
    { enabled }
  );

  return response.data;
}