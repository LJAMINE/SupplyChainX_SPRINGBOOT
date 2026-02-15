import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface Supplier {
  id: number;
  name: string;
  contact: string;
  rating: number;
  leadTime: number;
  createdAt: string;
  updatedAt: string;
}

export interface SupplierForm {
  name: string;
  contact: string;
  rating: number | null;
  leadTime: number;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

@Injectable({ providedIn: 'root' })
export class SuppliersService {
  private readonly base = `${environment.apiUrl}/api/suppliers`;

  constructor(private http: HttpClient) {}

  list(search?: string, page = 0, size = 20): Observable<PageResponse<Supplier>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (search?.trim()) params = params.set('s', search.trim());
    return this.http.get<PageResponse<Supplier>>(this.base, { params });
  }

  get(id: number): Observable<Supplier> {
    return this.http.get<Supplier>(`${this.base}/${id}`);
  }

  create(dto: SupplierForm): Observable<Supplier> {
    return this.http.post<Supplier>(this.base, dto);
  }

  update(id: number, dto: SupplierForm): Observable<Supplier> {
    return this.http.put<Supplier>(`${this.base}/${id}`, dto);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }
}
