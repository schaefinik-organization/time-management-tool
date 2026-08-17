const mockData = {
  '/user/report':     {
        "user": {
            "id": 12,
            "username": "m.muster",
            "role": "employee"
        },
        "totalHoursOverall": 38,
        "billableHoursOverall": 28,
        "projects": [
            {
                "projectId": 4,
                "projectName": "Test",
                "totalHours": 15,
                "billableHours": 15,
                "nonBillableHours": 0,
                "entryCount": 3
            },
            {
                "projectId": 6,
                "projectName": "Interne Weiterbildung",
                "totalHours": 10,
                "billableHours": 0,
                "nonBillableHours": 10,
                "entryCount": 1
            },
            {
                "projectId": 7,
                "projectName": "Kunden-Support",
                "totalHours": 13,
                "billableHours": 13,
                "nonBillableHours": 0,
                "entryCount": 6
            }
        ]
    },
  '/manager/report': [
    {
        "user": {
            "id": 1,
            "username": "admin",
            "role": "manager"
        },
        "totalHoursOverall": 41,
        "billableHoursOverall": 41,
        "projects": [
            {
                "projectId": 2,
                "projectName": "Test",
                "totalHours": 41,
                "billableHours": 41,
                "nonBillableHours": 0,
                "entryCount": 5
            }
        ]
    },
    {
        "user": {
            "id": 8,
            "username": "user",
            "role": "employee"
        },
        "totalHoursOverall": 44,
        "billableHoursOverall": 36,
        "projects": [
            {
                "projectId": 4,
                "projectName": "Test",
                "totalHours": 24,
                "billableHours": 24,
                "nonBillableHours": 0,
                "entryCount": 2
            },
            {
                "projectId": 5,
                "projectName": "andere Projekt",
                "totalHours": 20,
                "billableHours": 12,
                "nonBillableHours": 8,
                "entryCount": 4
            }
        ]
    },
    {
        "user": {
            "id": 12,
            "username": "m.muster",
            "role": "employee"
        },
        "totalHoursOverall": 38,
        "billableHoursOverall": 28,
        "projects": [
            {
                "projectId": 4,
                "projectName": "Test",
                "totalHours": 15,
                "billableHours": 15,
                "nonBillableHours": 0,
                "entryCount": 3
            },
            {
                "projectId": 6,
                "projectName": "Interne Weiterbildung",
                "totalHours": 10,
                "billableHours": 0,
                "nonBillableHours": 10,
                "entryCount": 1
            },
            {
                "projectId": 7,
                "projectName": "Kunden-Support",
                "totalHours": 13,
                "billableHours": 13,
                "nonBillableHours": 0,
                "entryCount": 6
            }
        ]
    },
    {
        "user": {
            "id": 15,
            "username": "l.schmidt",
            "role": "employee"
        },
        "totalHoursOverall": 12,
        "billableHoursOverall": 10,
        "projects": [
            {
                "projectId": 5,
                "projectName": "andere Projekt",
                "totalHours": 12,
                "billableHours": 10,
                "nonBillableHours": 2,
                "entryCount": 2
              }
          ]
      }
    ]
}

export default mockData
