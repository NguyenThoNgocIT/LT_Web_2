// src/components/ui/Table.jsx
import React from 'react';

const Table = ({ children, className = '', ...props }) => {
    return (
        <div className="overflow-x-auto">
            <table className={`min-w-full divide-y divide-gray-200 ${className}`} {...props}>
                {children}
            </table>
        </div>
    );
};

const TableHeader = ({ children, className = '', ...props }) => {
    return (
        <thead className={`bg-gray-50 ${className}`} {...props}>
        {children}
        </thead>
    );
};

const TableBody = ({ children, className = '', ...props }) => {
    return (
        <tbody className={`bg-white divide-y divide-gray-200 ${className}`} {...props}>
        {children}
        </tbody>
    );
};

const TableRow = ({ children, className = '', ...props }) => {
    return (
        <tr className={className} {...props}>
            {children}
        </tr>
    );
};

const TableCell = ({ children, className = '', isHeader = false, ...props }) => {
    const baseClasses = isHeader
        ? 'px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider'
        : 'px-6 py-4 whitespace-nowrap text-sm text-gray-900';

    return (
        <td className={`${baseClasses} ${className}`} {...props}>
            {children}
        </td>
    );
};

Table.Header = TableHeader;
Table.Body = TableBody;
Table.Row = TableRow;
Table.Cell = TableCell;

export default Table;