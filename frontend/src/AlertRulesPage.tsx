import { useEffect, useMemo, useState } from 'react';
import {
  Activity, AlertTriangle, Bell, Check, ChevronDown, CircleHelp,
  Clock3, LayoutDashboard, Plus, Search, Server as ServerIcon,
  Settings, ShieldCheck, SlidersHorizontal, ToggleLeft, ToggleRight, X
} from 'lucide-react';

import {
  AlertRule,
  Severity,
  getAlertRules,
  createAlertRule,
  setAlertRuleEnabled
} from './alertRulesApi';

export function AlertRulesPage({ onNavigate }: { onNavigate: () => void }) {
  const [rules, setRules] = useState<AlertRule[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [saving, setSaving] = useState(false);
  const [updatingRuleId, setUpdatingRuleId] = useState<string | null>(null);

  const [search, setSearch] = useState('');
  const [severityFilter, setSeverityFilter] = useState('ALL');
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [showModal, setShowModal] = useState(false);
  const [formError, setFormError] = useState('');

  const [form, setForm] = useState({
    name: '',
    description: '',
    metric: 'CPU' as 'CPU' | 'MEMORY',
    threshold: '80',
    severity: 'WARNING' as Severity
  });

  const loadRules = async (showLoading = false) => {
    if (showLoading) {
      setLoading(true);
    }

    try {
      const data = await getAlertRules();
      setRules(data);
      setError('');
    } catch (err) {
      setError(
        err instanceof Error
          ? err.message
          : 'Failed to load alert rules.'
      );
    } finally {
      if (showLoading) {
        setLoading(false);
      }
    }
  };

  useEffect(() => {
    void loadRules(true);

    const intervalId = window.setInterval(() => {
      void loadRules(false);
    }, 15000);

    return () => window.clearInterval(intervalId);
  }, []);

  const activeCount = rules.filter(rule => rule.enabled).length;
  const disabledCount = rules.length - activeCount;
  const triggeredCount = rules.filter(rule => rule.lastTriggered !== null).length;

  const filteredRules = useMemo(() => {
    const query = search.trim().toLowerCase();

    return rules.filter(rule => {
      const matchesSearch =
        rule.name.toLowerCase().includes(query) ||
        rule.description.toLowerCase().includes(query) ||
        rule.metric.toLowerCase().includes(query);

      const matchesSeverity =
        severityFilter === 'ALL' || rule.severity === severityFilter;

      const matchesStatus =
        statusFilter === 'ALL' ||
        (statusFilter === 'ACTIVE' && rule.enabled) ||
        (statusFilter === 'DISABLED' && !rule.enabled);

      return matchesSearch && matchesSeverity && matchesStatus;
    });
  }, [rules, search, severityFilter, statusFilter]);

  const toggleRule = async (rule: AlertRule) => {
    setUpdatingRuleId(rule.id);
    setError('');

    try {
      const updatedRule = await setAlertRuleEnabled(rule.id, !rule.enabled);

      setRules(current =>
        current.map(item => item.id === updatedRule.id ? updatedRule : item)
      );
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to update the rule.');
    } finally {
      setUpdatingRuleId(null);
    }
  };

  const createRule = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setFormError('');

    const name = form.name.trim();
    const threshold = Number(form.threshold);

    if (!name) {
      setFormError('Rule name is required.');
      return;
    }

    if (!Number.isFinite(threshold) || threshold < 1 || threshold > 100) {
      setFormError('Threshold must be between 1 and 100.');
      return;
    }

    setSaving(true);

    try {
      const newRule = await createAlertRule({
        name,
        description: form.description.trim() || 'Custom monitoring rule',
        metric: form.metric,
        operator: '>',
        threshold,
        severity: form.severity
      });

      setRules(current => [newRule, ...current]);
      setSearch('');
      setSeverityFilter('ALL');
      setStatusFilter('ALL');
      setForm({
        name: '',
        description: '',
        metric: 'CPU' as 'CPU' | 'MEMORY',
        threshold: '80',
        severity: 'WARNING'
      });
      setShowModal(false);
    } catch (err) {
      setFormError(err instanceof Error ? err.message : 'Failed to create the rule.');
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="shell">
      <aside className="sidebar">
        <div className="brand">
          <div className="brand-mark"><Activity size={19} /></div>
          <div><b>sentinel</b><small>INFRASTRUCTURE</small></div>
        </div>

        <div className="workspace">
          <span className="workspace-dot" />
          Sentinel workspace
          <ChevronDown size={14} />
        </div>

        <div className="nav-label">WORKSPACE</div>
        <nav>
          <a href="#overview" onClick={(event) => { event.preventDefault(); onNavigate(); }}><LayoutDashboard size={17} /> Overview</a>
          <a href="#servers"><ServerIcon size={17} /> Servers</a>
          <a href="#incidents"><AlertTriangle size={17} /> Incidents</a>
          <a className="active" href="#rules"><Bell size={17} /> Alert rules</a>
        </nav>

        <div className="nav-label">PREFERENCES</div>
        <nav>
          <a href="#settings"><Settings size={17} /> Settings</a>
          <a href="#docs"><CircleHelp size={17} /> Documentation</a>
        </nav>

        <div className="sidebar-bottom">
          <div className="avatar">MA</div>
          <div><b>Mozgalova A. V.</b><small>Project Administrator</small></div>
          <ChevronDown size={15} />
        </div>
      </aside>

      <main className="main rules-main">
        <header className="topbar">
          <div>
            <span className="eyebrow">MONITORING / CONFIGURATION</span>
            <h1>Alert rules</h1>
            <p>Configure conditions that trigger alerts across your infrastructure.</p>
          </div>
          <div className="top-actions">
            <span className="rules-mode"><i /> Rule configuration</span>
            <button className="primary" onClick={() => {
              setFormError('');
              setShowModal(true);
            }}>
              <Plus size={16} /> Create rule
            </button>
          </div>
        </header>

        {error && (
          <div className="rules-error" role="alert">
            <span>{error}</span>
            <button className="text-action" onClick={() => void loadRules()}>Retry</button>
          </div>
        )}

        <section className="stats rules-stats">
          <div className="stat">
            <div className="stat-top"><span>ACTIVE RULES</span><i><ShieldCheck /></i></div>
            <strong>{loading ? '—' : activeCount}</strong>
            <small>Currently monitoring</small>
          </div>
          <div className="stat">
            <div className="stat-top"><span>TRIGGERED</span><i><AlertTriangle /></i></div>
            <strong>{loading ? '—' : triggeredCount}</strong>
            <small>Rules with recorded activity</small>
          </div>
          <div className="stat">
            <div className="stat-top"><span>DISABLED</span><i><ToggleLeft /></i></div>
            <strong>{loading ? '—' : disabledCount}</strong>
            <small>Not currently monitoring</small>
          </div>
          <div className="stat">
            <div className="stat-top"><span>TOTAL RULES</span><i><Bell /></i></div>
            <strong>{loading ? '—' : rules.length}</strong>
            <small>Configured in this workspace</small>
          </div>
        </section>

        <section className="panel rules-panel">
          <div className="rules-heading">
            <div>
              <h2>Monitoring rules</h2>
              <p>Manage alert conditions and notification thresholds.</p>
            </div>
            <span className="rules-total">
              {loading ? 'Loading...' : `${filteredRules.length} of ${rules.length} rules`}
            </span>
          </div>

          <div className="rules-toolbar">
            <div className="rules-search">
              <Search size={16} />
              <input
                value={search}
                onChange={event => setSearch(event.target.value)}
                placeholder="Search rules..."
                aria-label="Search rules"
                disabled={loading}
              />
              {search && (
                <button className="clear-search" onClick={() => setSearch('')} aria-label="Clear search">
                  <X size={14} />
                </button>
              )}
            </div>

            <div className="filter-control">
              <SlidersHorizontal size={15} />
              <select
                value={severityFilter}
                onChange={event => setSeverityFilter(event.target.value)}
                disabled={loading}
              >
                <option value="ALL">All severities</option>
                <option value="WARNING">Warning</option>
                <option value="CRITICAL">Critical</option>
              </select>
            </div>

            <select
              className="status-filter"
              value={statusFilter}
              onChange={event => setStatusFilter(event.target.value)}
              aria-label="Filter by status"
              disabled={loading}
            >
              <option value="ALL">All statuses</option>
              <option value="ACTIVE">Active</option>
              <option value="DISABLED">Disabled</option>
            </select>
          </div>

          <div className="rules-table-wrap">
            <div className="rules-table rules-table-header">
              <span>RULE</span>
              <span>CONDITION</span>
              <span>SEVERITY</span>
              <span>LAST TRIGGERED</span>
              <span>STATUS</span>
              <span />
            </div>

            {loading && (
              <div className="rules-empty">
                <Clock3 size={22} />
                <b>Loading alert rules...</b>
                <span>Retrieving monitoring configuration.</span>
              </div>
            )}

            {!loading && filteredRules.map(rule => (
              <div className="rules-table rules-row" key={rule.id}>
                <div className="rule-name">
                  <span className={`rule-icon ${rule.severity.toLowerCase()}`}>
                    <AlertTriangle size={15} />
                  </span>
                  <span>
                    <b>{rule.name}</b>
                    <small>{rule.description}</small>
                  </span>
                </div>

                <div className="rule-condition">
                  <span>{rule.metric === 'CPU' ? 'CPU usage' : 'Memory usage'}</span>
                  <b>{rule.operator} {rule.threshold}%</b>
                </div>

                <div>
                  <span className={`severity-pill ${rule.severity.toLowerCase()}`}>
                    <i /> {rule.severity}
                  </span>
                </div>

                <div className="rule-last">
                  <Clock3 size={13} />
                  {rule.lastTriggered ? new Date(rule.lastTriggered).toLocaleString() : 'Never'}
                </div>

                <div>
                  <span className={`rule-status ${rule.enabled ? 'enabled' : 'disabled'}`}>
                    <i /> {rule.enabled ? 'Enabled' : 'Disabled'}
                  </span>
                </div>

                <button
                  className={`toggle-rule ${rule.enabled ? 'on' : ''}`}
                  onClick={() => void toggleRule(rule)}
                  disabled={updatingRuleId === rule.id}
                  aria-label={`${rule.enabled ? 'Disable' : 'Enable'} ${rule.name}`}
                  title={rule.enabled ? 'Disable rule' : 'Enable rule'}
                >
                  {updatingRuleId === rule.id
                    ? <Clock3 size={19} />
                    : rule.enabled ? <ToggleRight size={23} /> : <ToggleLeft size={23} />}
                </button>
              </div>
            ))}

            {!loading && !error && filteredRules.length === 0 && (
              <div className="rules-empty">
                <Search size={22} />
                <b>No rules found</b>
                <span>Try changing the search query or filters.</span>
                <button className="text-action" onClick={() => {
                  setSearch('');
                  setSeverityFilter('ALL');
                  setStatusFilter('ALL');
                }}>Clear filters</button>
              </div>
            )}
          </div>

          <div className="rules-footnote">
            <span><Check size={14} /> Changes are saved to the backend.</span>
            <span>Evaluation interval: 15 seconds</span>
          </div>
        </section>

        <footer>
          Sentinel · Server Monitoring UI
          <span>Alert rule configuration</span>
        </footer>
      </main>

      {showModal && (
        <div className="modal-backdrop" onClick={() => {
          if (!saving) setShowModal(false);
        }}>
          <form className="modal rule-modal" onSubmit={createRule} onClick={event => event.stopPropagation()}>
            <div className="modal-head">
              <div>
                <h2>Create alert rule</h2>
                <p>Define a condition for monitoring your infrastructure.</p>
              </div>
              <button
                type="button"
                className="icon-btn"
                disabled={saving}
                onClick={() => setShowModal(false)}
              >
                <X size={17} />
              </button>
            </div>

            <label>
              Rule name
              <input
                required
                maxLength={80}
                placeholder="e.g. High CPU utilization"
                value={form.name}
                onChange={event => setForm({ ...form, name: event.target.value })}
              />
            </label>

            <label>
              Description
              <input
                placeholder="Describe when this rule should trigger"
                value={form.description}
                onChange={event => setForm({ ...form, description: event.target.value })}
              />
            </label>

            <label>
              Metric
              <select
                value={form.metric}
                onChange={event => setForm({
                  ...form,
                  metric: event.target.value as 'CPU' | 'MEMORY'
                })}
              >
                <option value="CPU">CPU usage</option>
                <option value="MEMORY">Memory usage</option>
              </select>
            </label>

            <div className="rule-form-grid">
              <label>
                Threshold (%)
                <input
                  required
                  type="number"
                  min="1"
                  max="100"
                  value={form.threshold}
                  onChange={event => setForm({ ...form, threshold: event.target.value })}
                />
              </label>
              <label>
                Severity
                <select
                  value={form.severity}
                  onChange={event => setForm({ ...form, severity: event.target.value as Severity })}
                >
                  <option value="WARNING">Warning</option>
                  <option value="CRITICAL">Critical</option>
                </select>
              </label>
            </div>

            {formError && <div className="rules-error" role="alert">{formError}</div>}

            <div className="modal-actions">
              <button
                type="button"
                className="secondary"
                disabled={saving}
                onClick={() => setShowModal(false)}
              >Cancel</button>
              <button className="primary" disabled={saving}>
                <Plus size={15} /> {saving ? 'Creating...' : 'Create rule'}
              </button>
            </div>
          </form>
        </div>
      )}
    </div>
  );
}