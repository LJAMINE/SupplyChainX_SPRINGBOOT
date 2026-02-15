import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface RawMaterial {
  id: number;
  name: string;
  stock: number;
  stockMin: number;
  unit: string;
  createdAt: string;
}

export interface RawMaterialForm {
  name: string;
  stock: number;
  stockMin: number;
  unit: string;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

@Injectable({ providedIn: 'root' })
export class RawMaterialsService {
  private readonly base = `${environment.apiUrl}/api/raw-materials`;

  constructor(private http: HttpClient) {}

  list(search?: string, page = 0, size = 20): Observable<PageResponse<RawMaterial>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (search?.trim()) params = params.set('s', search.trim());
    return this.http.get<PageResponse<RawMaterial>>(this.base, { params });
  }

  get(id: number): Observable<RawMaterial> {
    return this.http.get<RawMaterial>(`${this.base}/${id}`);
  }

  create(dto: RawMaterialForm): Observable<RawMaterial> {
    return this.http.post<RawMaterial>(this.base, dto);
  }

  update(id: number, dto: RawMaterialForm): Observable<RawMaterial> {
    return this.http.put<RawMaterial>(`${this.base}/${id}`, dto);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }
}
