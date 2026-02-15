import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  RawMaterialsService,
  RawMaterial,
  RawMaterialForm,
} from './raw-materials.service';

@Component({
  selector: 'app-raw-materials',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="page">
      <div class="page-header">
        <h1>Raw Materials</h1>
        <button type="button" class="btn-primary" (click)="openCreateModal()">+ New Raw Material</button>
      </div>

      <div class="toolbar">
        <input
          type="text"
          class="search-input"
          placeholder="Search..."
          [(ngModel)]="search"
          (input)="loadPage(0)"
        />
      </div>

      <div class="table-wrap">
        <table class="table">
          <thead>
            <tr>
              <th>Name</th>
              <th>Stock</th>
              <th>Min Stock</th>
              <th>Unit</th>
              <th>Created</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            @for (item of items; track item.id) {
              <tr>
                <td>{{ item.name }}</td>
                <td>
                  <span [class.low-stock]="item.stock <= item.stockMin">{{ item.stock }}</span>
                </td>
                <td>{{ item.stockMin }}</td>
                <td>{{ item.unit || '-' }}</td>
                <td>{{ item.createdAt | date:'short' }}</td>
                <td class="actions">
                  <button type="button" class="btn-icon" (click)="openEditModal(item)" title="Edit">✎</button>
                  <button type="button" class="btn-icon btn-danger" (click)="confirmDelete(item)" title="Delete">✕</button>
                </td>
              </tr>
            } @empty {
              <tr><td colspan="6" class="empty">No raw materials found</td></tr>
            }
          </tbody>
        </table>
      </div>

      <div class="pagination">
        <button [disabled]="page <= 0" (click)="loadPage(page - 1)">← Prev</button>
        <span>Page {{ page + 1 }} of {{ totalPages || 1 }}</span>
        <button [disabled]="page >= (totalPages || 1) - 1" (click)="loadPage(page + 1)">Next →</button>
      </div>
    </div>

    <!-- Create/Edit Modal -->
    @if (showModal) {
      <div class="modal-backdrop" (click)="closeModal()">
        <div class="modal" (click)="$event.stopPropagation()">
          <h2>{{ editId ? 'Edit' : 'New' }} Raw Material</h2>
          <form (ngSubmit)="save()" #f="ngForm">
            @if (modalError) { <div class="modal-error">{{ modalError }}</div> }
            <div class="field">
              <label>Name *</label>
              <input [(ngModel)]="form.name" name="name" required />
            </div>
            <div class="field-row">
              <div class="field">
                <label>Stock *</label>
                <input type="number" [(ngModel)]="form.stock" name="stock" min="0" required />
              </div>
              <div class="field">
                <label>Min Stock *</label>
                <input type="number" [(ngModel)]="form.stockMin" name="stockMin" min="0" required />
              </div>
            </div>
            <div class="field">
              <label>Unit</label>
              <input [(ngModel)]="form.unit" name="unit" placeholder="e.g. kg, L" />
            </div>
            <div class="modal-actions">
              <button type="button" class="btn-secondary" (click)="closeModal()">Cancel</button>
              <button type="submit" class="btn-primary" [disabled]="f.invalid || saving">{{ saving ? 'Saving...' : 'Save' }}</button>
            </div>
          </form>
        </div>
      </div>
    }

    <!-- Delete confirmation -->
    @if (deleteTarget) {
      <div class="modal-backdrop" (click)="cancelDelete()">
        <div class="modal modal-sm" (click)="$event.stopPropagation()">
          <h2>Delete "{{ deleteTarget.name }}"?</h2>
          <p>This action cannot be undone.</p>
          <div class="modal-actions">
            <button type="button" class="btn-secondary" (click)="cancelDelete()">Cancel</button>
            <button type="button" class="btn-danger" (click)="doDelete()" [disabled]="deleting">{{ deleting ? 'Deleting...' : 'Delete' }}</button>
          </div>
        </div>
      </div>
    }
  `,
  styles: [`
    .page { max-width: 1000px; margin: 0 auto; }
    .page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem; }
    .page-header h1 { margin: 0; font-size: 1.5rem; }
    .toolbar { margin-bottom: 1rem; }
    .search-input {
      padding: 0.5rem 0.75rem; width: 280px; border: 1px solid #475569; border-radius: 6px;
      background: #1e293b; color: #e2e8f0;
    }
    .table-wrap { overflow-x: auto; border-radius: 8px; border: 1px solid #334155; }
    .table { width: 100%; border-collapse: collapse; }
    .table th, .table td { padding: 0.75rem 1rem; text-align: left; border-bottom: 1px solid #334155; }
    .table th { background: #1e293b; color: #94a3b8; font-weight: 600; font-size: 0.8rem; text-transform: uppercase; }
    .table td { background: #0f172a; }
    .table tbody tr:hover td { background: #1e293b; }
    .low-stock { color: #f87171; font-weight: 600; }
    .empty { color: #64748b; font-style: italic; padding: 2rem !important; }
    .actions { white-space: nowrap; }
    .btn-icon {
      background: none; border: none; color: #94a3b8; padding: 0.35rem 0.5rem; margin-right: 0.25rem;
      cursor: pointer; font-size: 1rem; border-radius: 4px;
    }
    .btn-icon:hover { background: #334155; color: #e2e8f0; }
    .btn-icon.btn-danger:hover { background: #7f1d1d; color: #fca5a5; }
    .pagination {
      display: flex; align-items: center; gap: 1rem; margin-top: 1rem;
    }
    .pagination button {
      padding: 0.4rem 0.75rem; background: #334155; color: #e2e8f0; border: none; border-radius: 6px;
    }
    .pagination button:disabled { opacity: 0.5; cursor: not-allowed; }
    .btn-primary { padding: 0.5rem 1rem; background: #38bdf8; color: #0f172a; border: none; border-radius: 6px; font-weight: 600; }
    .btn-primary:hover:not(:disabled) { background: #7dd3fc; }
    .btn-secondary { padding: 0.5rem 1rem; background: #334155; color: #e2e8f0; border: none; border-radius: 6px; }
    .btn-secondary:hover { background: #475569; }
    .btn-danger { padding: 0.5rem 1rem; background: #dc2626; color: white; border: none; border-radius: 6px; }
    .btn-danger:hover:not(:disabled) { background: #ef4444; }
    .modal-backdrop { position: fixed; inset: 0; background: rgba(0,0,0,0.6); display: flex; align-items: center; justify-content: center; z-index: 1000; padding: 1rem; }
    .modal {
      background: #1e293b; border-radius: 12px; padding: 1.5rem; max-width: 440px; width: 100%;
      border: 1px solid #334155;
    }
    .modal h2 { margin: 0 0 1rem; font-size: 1.25rem; }
    .modal-error { padding: 0.5rem; background: rgba(239,68,68,0.15); color: #fca5a5; border-radius: 6px; margin-bottom: 1rem; font-size: 0.9rem; }
    .field { margin-bottom: 1rem; }
    .field label { display: block; margin-bottom: 0.35rem; font-size: 0.875rem; color: #94a3b8; }
    .field input { width: 100%; padding: 0.6rem; border: 1px solid #475569; border-radius: 6px; background: #0f172a; color: #e2e8f0; }
    .field-row { display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; }
    .modal-actions { display: flex; justify-content: flex-end; gap: 0.75rem; margin-top: 1.25rem; }
    .modal-sm .modal-actions { margin-top: 1rem; }
  `],
})
export class RawMaterialsComponent implements OnInit {
  private service = inject(RawMaterialsService);

  items: RawMaterial[] = [];
  search = '';
  page = 0;
  totalPages = 1;

  showModal = false;
  editId: number | null = null;
  form: RawMaterialForm = { name: '', stock: 0, stockMin: 0, unit: '' };
  saving = false;
  modalError = '';

  deleteTarget: RawMaterial | null = null;
  deleting = false;

  ngOnInit(): void {
    this.loadPage(0);
  }

  loadPage(p: number): void {
    this.page = p;
    this.service.list(this.search, this.page, 20).subscribe({
      next: (res) => {
        this.items = res.content;
        this.totalPages = res.totalPages;
      },
    });
  }

  openCreateModal(): void {
    this.editId = null;
    this.form = { name: '', stock: 0, stockMin: 0, unit: '' };
    this.modalError = '';
    this.showModal = true;
  }

  openEditModal(item: RawMaterial): void {
    this.editId = item.id;
    this.form = {
      name: item.name,
      stock: item.stock,
      stockMin: item.stockMin,
      unit: item.unit || '',
    };
    this.modalError = '';
    this.showModal = true;
  }

  closeModal(): void {
    this.showModal = false;
  }

  save(): void {
    if (this.saving) return;
    this.saving = true;
    this.modalError = '';

    const req = this.editId
      ? this.service.update(this.editId, this.form)
      : this.service.create(this.form);

    req.subscribe({
      next: () => {
        this.saving = false;
        this.closeModal();
        this.loadPage(this.page);
      },
      error: (err) => {
        this.saving = false;
        this.modalError = err?.error?.message || 'An error occurred';
      },
    });
  }

  confirmDelete(item: RawMaterial): void {
    this.deleteTarget = item;
  }

  cancelDelete(): void {
    this.deleteTarget = null;
  }

  doDelete(): void {
    if (!this.deleteTarget || this.deleting) return;
    this.deleting = true;
    this.service.delete(this.deleteTarget.id).subscribe({
      next: () => {
        this.deleting = false;
        this.deleteTarget = null;
        this.loadPage(this.page);
      },
      error: () => { this.deleting = false; },
    });
  }
}
